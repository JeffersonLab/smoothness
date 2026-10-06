# Agent guidelines

Instructions for AI coding agents working in this repository. See `README.md` for what the
project is, its settings, and how to set it up, and [docs/agent-vm.md](docs/agent-vm.md) for
setting it up in an agent VM and where it stands on readiness for agents.

## Development

How to check your own work here. Check each change with these commands before you call it done,
fix what fails, and report what you ran and what it returned.

- Two Gradle subprojects: `smoothness-weblib`, the library (Java classes, JSP tags, and the
  JavaScript and CSS under `src/main/resources/META-INF`), and `smoothness-demo`, a web app that
  shows it off and is how you see a library change working.
- Set up: JDK 21 and Docker; use `./gradlew`, which pins Gradle. Services: Oracle, Keycloak,
  Puppet Show, MailHog, and the demo built from the working tree, all by `build.yaml`:
  `docker compose -f build.yaml up -d --build --wait` (about 100 s from nothing; it returns once
  all are healthy). Ports are in the README's Ports section, each with a `SMOOTHNESS_*_PORT`
  setting; check what's already listening (`ss -ltnp`) first.
- On every change, run `./gradlew build` (about 30 s cold, seconds warm). It compiles both
  subprojects, runs `spotlessCheck` (google-java-format for Java; `./gradlew spotlessApply`
  formats), and runs the weblib's unit tests in `smoothness-weblib/src/test`. To run one class:
  `./gradlew :smoothness-weblib:test --tests '*TimeUtilTest'`. The tests run in
  America/New_York and US English (set in `build.gradle`), since `TimeUtil` and the parameter
  converters use the JVM's default time zone and locale.
- The unit tests are characterization tests: they record what the code does now, quirks
  included (such as `isSameMonth` ignoring the year). When you change behavior on purpose, change
  the test that records it and say so in the pull request. Add or update tests with each change,
  check that a new test fails without your fix, and never skip or weaken a test to make it pass.
  Test servlet code with `FakeRequest` (parameters and paths only) rather than a mocking library.
- Before opening a pull request, rebuild and restart the demo, since the container runs the
  image and not your working tree: `docker compose -f build.yaml up -d --build --wait demo`
  (about 1 minute). Then run the integration tests, `./gradlew integrationTest` (a few seconds;
  `--tests '*MoviesIT'` runs one class). They drive the running demo over HTTPS as a browser
  would, logging in through Keycloak's form, and read MailHog's API; they use the ports in `.env`.
  They add a movie and change a setting, and put both back. Check the pages you changed, and
  `docker compose -f build.yaml logs demo` for errors. For changes to tags, also run
  `./gradlew javadoc`, which builds the tag library docs too.
- Integration tests are in `smoothness-demo/src/integration`; `Demo.Session` is a browser with its
  own cookies (`Session.loggedIn("jdoe")`). Give data a test creates a unique name, and remove or
  restore it, even when the test fails.
- Test data: `container/oracle/initdb.d` builds the database when the Oracle container is
  created (the demo's movies and the settings). `docker compose -f build.yaml down -v` deletes it;
  the next `up` rebuilds it. Each clone or worktree gets its own containers, named after its
  directory, but needs its own ports in a `.env` (see the README); a stack uses about 4 GB of
  memory. Never use production data.
- Accounts (Keycloak test realm, password `password`): `jdoe` and `tbrown` are admins
  (`smoothness-demo-admin`); `jadams` and `jsmith` are users. A login made before Keycloak is
  healthy gets a session without its roles: log in again.
- Outside systems: Keycloak (login and the user directory), Oracle, Puppet Show (HTML to PDF and
  images), and SMTP email all run as containers; MailHog catches the email
  (`http://localhost:8025`). The JLab logbook and run-dates service are unset in the demo. Never
  point the app at JLab's servers or production databases.
- The library is published to Maven Central and used by many JLab apps, which declare its
  version, as `smoothness-template` shows. Keep its public API (Java classes, tag attributes,
  `smoothness.js` functions, CSS classes, and the `SETTING` table) compatible; say in the pull
  request when a change breaks it or needs a step in apps that use it.
- Compile for Java 17 (`options.release = 17`). Wildfly provides the dependencies
  (`compileOnly` and `providedCompile`); don't bundle them in the war. Only the weblib itself is
  packaged in apps' wars.
- New tag files go in `smoothness-weblib/src/main/resources/META-INF/tags` and must be declared
  in `META-INF/smoothness.tld`. Third-party libraries under `META-INF/resources/resources` (jQuery,
  jQuery UI, select2, Flot) are vendored, minified releases: don't edit them; upgrade by adding the
  new version's files.
- Schema and settings changes go in `container/oracle/initdb.d`; there are no migrations, so
  describe the SQL that existing databases need in the pull request.
- Never commit secrets. Local port settings go in `.env`, which git ignores.

## Commit identity

Commits written by an agent must say so. Before committing, check that the repository-local
identity (never `--global`) is that of the GitHub App bot you push with, so GitHub links the
commits to it and squash commits show the same name. Never commit under a person's name or
email. Name the agent and model in a trailer: keep the one your tool adds (such as
`Co-Authored-By`), or else add `Assisted-by: <agent>:<model>`.

## Commits

- Imperative summary line, then a body explaining what changed and why.
- Squash merges use the commit messages: write the first one for `main`.
- Commit and push only when asked.

## Branches and pull requests

- Start each task on a new branch from an up-to-date `main`; target `main`. Work only in your
  own clone or worktree.
- Label every pull request `source::ai`, and make the person who reviews it both reviewer and
  assignee (ask for their username if you don't know it). In gh 2.46, `gh pr edit` fails; add
  labels and retarget with `gh api` (see the coding-agents Git workflow guide's tool notes).
- You cannot add items to the JeffersonLab organization's project: when you report back, list
  the pull requests and issues you opened, so the person can add them to the period's project.
- Never change `VERSION` unless asked: a change to it on `main` releases the project (tag, GitHub
  release with the demo war, the weblib on Maven Central, the docs, the demo Docker image, and a
  deploy to JLab's test server). When asked, open a pull request that changes only `VERSION`,
  and write any upgrade steps in its description for the maintainer to add to the release.
- A person reviews and merges, and merged branches are deleted; never merge, approve, or enable
  auto-merge yourself.
- Before pushing to a pull request's branch, check that it is still open: commits pushed after it
  merged never reach `main`, so put them in a new pull request.
- To build on a pull request still in review, branch from its branch and target that branch,
  saying so in the description. Once the first merges, check that yours now targets `main`, and
  retarget it if not.
- Describe what changed, any deployment steps, and the checks you ran, including what you could
  not run. Add a short Decisions part when the person questioned or changed something, or when
  alternatives were dropped.
- After pushing, wait for the pull request's checks to finish before reporting it ready (`build`
  from CI, and CodeQL); `gh pr checks <number> --watch` waits. Read failed jobs' logs and fix the
  cause; never skip or weaken a check to pass it. Report how they ended, and say if one failed
  for a reason outside your change.
- Name the issue a change is for in the commit body: `Fixes #<issue>`, or `Part of #<issue>` if
  some of it stays open. Work you were asked to do needs no issue.
- For something outside your task (another project, code another agent is working on, or a
  change that needs a decision), don't fix it in passing: offer to open an issue, labeled
  `source::ai` and assigned to the person you work with, with what you found, the evidence, and a
  suggested fix.
- When asked to address a review, reply in each thread with what you changed, and push new
  commits rather than rewriting ones already reviewed; the reviewer resolves the threads.
- When the person corrects you on something any agent here should know, propose adding it to
  this file, or better a check that catches it. Keep one developer's preferences out of it.
