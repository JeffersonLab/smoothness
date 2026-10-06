# smoothness in an agent VM

Coding agents work on smoothness in an agent VM, apart from your own checkout and containers.
Build the VM once with the
[agent VM setup guide](https://code.jlab.org/acc/iac/docs/coding-agents/-/blob/main/docs/vm-setup.md)
in the coding-agents repository; one VM serves all your projects. This page adds smoothness to
it. Commands marked **host** run on your computer; **VM** commands run inside the VM.

## Before you start

- The VM's GitHub App is installed on `JeffersonLab/smoothness`
  ([setup step 9](https://code.jlab.org/acc/iac/docs/coding-agents/-/blob/main/docs/vm-setup.md#step-9-github-access)).
- `main` has a ruleset that requires a pull request with one approval, dismisses stale approvals,
  and blocks force pushes and deletion, with no bypass; merged branches are deleted
  automatically; and the CD workflow runs only for `main`, as the
  [Git workflow guide](https://code.jlab.org/acc/iac/docs/coding-agents/-/blob/main/docs/git-workflow.md#set-up-a-project)
  describes. The repository has the `source::ai` label.

## Clone, install, and run the checks

**VM** (`ssh agent-vm`):

```bash
github-app-clone JeffersonLab/smoothness
sudo apt-get install -y openjdk-21-jdk-headless
cd ~/smoothness
./gradlew build
docker compose -f build.yaml up -d --build --wait
```

`github-app-clone` sets the clone's commit name and email to the App's bot. The setup guide's
base script already installed Docker and git.

On the first VM (6 CPUs, 11 GB of memory), `./gradlew build` took 30 seconds cold, with the
Gradle download, and 3 seconds after `./gradlew clean` with the unit tests (114 of them, under a
second). `up --build --wait` took about 100
seconds from nothing, with the images already pulled: building the demo image takes about 45
seconds, Oracle and Keycloak are healthy about a minute after they start, and the demo starts
once both are. Rebuilding the demo after a change
(`docker compose -f build.yaml up -d --build --wait demo`) took 53 seconds. The stack used about
3.6 GB of memory, 2.3 GB of it Oracle's.

## Ports and sister agents

The containers publish the ports in the README's Ports section on 127.0.0.1, each with a
`SMOOTHNESS_*_PORT` setting; the defaults (8080, 8443, 8081, 1521, and others) are the same as
adm's. Check what's listening first (`ss -ltnp`), and for a second copy, or alongside adm, set
other ports in a `.env` next to `compose.yaml`.

Compose names the containers, network, and volumes after the directory (`smoothness-oracle-1`),
so a sister agent's worktree gets its own stack and database once it has its own ports: nothing
is shared, and `down -v` in one worktree doesn't touch another's. Each stack needs about 4 GB of
memory, so two fit in an 11 GB VM, alongside little else.

## See the demo in your browser

Forward the app and Keycloak (**host**), since logins redirect to `localhost:8081` and back to
`localhost:8443`:

```bash
ssh -N -L 8080:localhost:8080 -L 8443:localhost:8443 -L 8081:localhost:8081 agent-vm
```

Open `http://localhost:8080/smoothness-demo` and log in as `jdoe` / `password`. MailHog's inbox
is at port 8025, if you forward it too.

## Readiness

Where smoothness stands on the
[readiness checklist](https://code.jlab.org/acc/iac/docs/coding-agents/-/blob/main/docs/project-readiness.md#readiness-checklist):

- [x] **Setup.** The commands above, from a fresh clone.
- [x] **Checks.** `./gradlew build` compiles, checks formatting, and runs the unit tests in
      seconds, and CI runs it.
- [x] **Readable failures.** Gradle and `docker compose` report in the terminal.
- [x] **Data.** `container/oracle/initdb.d` builds the database; no production data.
- [x] **Stand-ins.** Keycloak, Oracle, Puppet Show, and MailHog run as containers, and the demo
      leaves the logbook and run-dates services unset.
- [ ] **UI.** An agent can fetch pages with `curl`, but has no browser tests or screenshot
      script, and pages behind a login need a scripted Keycloak login. Puppet Show can't take
      screenshots in the VM (see Troubleshooting).
- [ ] **Tests.** Characterization tests cover the weblib's helpers (`TimeUtil`, the parameter
      converters and validators, `Paginator`, `ServletUtil`, and others). Not yet: the filters,
      services, servlets, tags, `smoothness.js`, and the demo.
- [x] **Instructions.** [AGENTS.md](../AGENTS.md).

## Troubleshooting

| Symptom | Cause and fix |
| --- | --- |
| `up` says a port is already allocated | Another project (adm uses the same defaults) or another copy of smoothness holds it; stop it, or set other ports in `.env` |
| A logged-in user gets 403 on `/setup` though they're an admin | They logged in before Keycloak had finished creating the test realm; log out and in again. `up --wait` avoids it |
| PDF and image export fail with HTTP 500; Puppet Show's log or page says `No usable sandbox!` | Chromium in the Puppet Show container can't create its sandbox: Ubuntu 26.04 sets `kernel.apparmor_restrict_unprivileged_userns=1`, which applies even to the privileged container. Not fixed in the VM; the rest of the demo works |
| `docker compose up` (without `-f build.yaml`) runs an older demo | The default files run the published `jeffersonlab/smoothness-demo:latest` image, not your working tree; use `-f build.yaml` |
