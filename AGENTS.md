# Agent guidelines

Instructions for AI coding agents working in this repository. See `README.md` for what the
project is, its settings, and how to set it up, and [docs/agent-vm.md](docs/agent-vm.md) for
setting it up in an agent VM and where it stands on readiness for agents.

## Development

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
  the test that records it and say so in the pull request.
  Test servlet code with `FakeRequest` (parameters and paths only), filters with `FakeHttp`
  (request, response, and chain), and settings with `TestSettings.cache`, rather than a mocking
  library.
- Before opening a pull request, rebuild and restart the demo, since the container runs the
  image and not your working tree: `docker compose -f build.yaml up -d --build --wait demo`
  (about 1 minute). Then run the integration tests, `./gradlew integrationTest` (about 30
  seconds; `--tests '*MoviesIT'` runs one class). Most drive the demo over HTTPS as a browser
  would; `BrowserIT` runs the pages in headless Chromium, through Playwright, and fails on any
  JavaScript error. `SmoothnessJsIT` calls `smoothness.js`'s functions on `/hello`, with
  Playwright's clock fixed (`page.clock().setFixedTime`) for anything that depends on today, and
  checks its page behavior: test changes to `smoothness.js` there. They use the ports in `.env`.
  They add movies and change a setting, and put them back. Check
  `docker compose -f build.yaml logs demo` for errors. For changes to tags,
  also run `./gradlew javadoc`, which builds the tag library docs too.
- UI changes: look at them. `./gradlew :smoothness-demo:screenshot -Ppage=/overview` saves a
  full-page screenshot of the running demo in `smoothness-demo/build/screenshots` and prints its
  path and any JavaScript errors; add `-Puser=jdoe` to log in first. Read the image, and for a
  change to behavior, add a `BrowserIT` test that clicks through it.
- Integration tests are in `smoothness-demo/src/integration`. `Demo.Session` is an HTTP client
  with its own cookies (`Session.loggedIn("jdoe")`); `DemoBrowser` gives Chromium pages
  (`browser.loggedIn("jdoe")`). Give data a test creates a unique name, and remove or restore it
  in a `finally`. The first run downloads Chromium to `~/.cache/ms-playwright`
  (`installChromium`).
- Test data: `container/oracle/initdb.d` builds the database when the Oracle container is
  created (the demo's movies and the settings). `docker compose -f build.yaml down -v` deletes it;
  the next `up` rebuilds it. Each clone or worktree gets its own containers, named after its
  directory, but needs its own ports in a `.env` (see the README); a stack uses about 4 GB of
  memory.
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
  in `META-INF/smoothness.tld`. Show a new tag or option on the demo's Features > Tags page
  (`/features/tags`) if no other page uses it, and test it there: markup in `TagsIT`, behavior
  in `TagsBrowserIT`. Third-party libraries under `META-INF/resources/resources` (jQuery,
  jQuery UI, select2, Flot) are vendored, minified releases: don't edit them; upgrade by adding the
  new version's files.
- Schema and settings changes go in `container/oracle/initdb.d`; there are no migrations, so
  describe the SQL that existing databases need in the pull request.
- Local port settings go in `.env`, which git ignores.

<!-- BEGIN coding-agents shared rules 6.2.0 (generated: edit in acc/iac/docs/coding-agents) -->

## Shared rules

These rules are the same in every project that uses them. Where this file's other sections say
otherwise, follow those.

On GitHub, "merge request" means "pull request", and "pipeline" means the pull request's
required checks.

### Checking your work

- Check each change with the Development section's commands before you call it done, fix what
  fails, and report what you ran and what it returned.
- Verify every change with checks appropriate to its behavior and regression risk. Add or update
  tests where that risk warrants them; meaningful existing coverage can suffice. For bug fixes,
  show the regression test fails without the fix and passes with it when practical; explain when
  this cannot be shown. For documentation or formatting, check links, examples, or formatting
  as applicable. Never skip or weaken a check merely to make it pass.
- For code with thin tests, use characterization tests where they help establish a baseline:
  they record existing behavior, not necessarily correct behavior. Separate them into a test-only
  merge request when risk or review needs warrant it; otherwise include them with the change.
- Never use production data, and never commit secrets.

### Task authorization

- Use the request and authorization already given in the session; do not ask again for an
  authorized action. “Look around and suggest improvements” means investigate, run local
  checks, and report; it does not authorize implementing or publishing the suggestions.
- An implementation request such as “Fix this bug” authorizes the local changes and verification
  needed to finish it. Complete them; ask before a material expansion of scope.
- Commit and push only when asked. “Create a merge request for issue #14” also authorizes
  implementation, verification, commits, pushing the branch, and opening the merge request.
  A local implementation request alone does not. A request to address review on that merge
  request authorizes the necessary follow-up commits and pushes.
- A requested review authorizes checks and posting findings as comments. Commit and push review
  fixes only when asked; follow the review conventions below.
- Releases, deployments, production data changes, and settings changes in external systems need
  authorization for the action and target environment. A request to implement a change or open
  a merge request alone does not give it. Follow the project's release, issue-filing, and access
  rules; never approve, merge, or enable auto-merge.
- Ask only when a consequential ambiguity remains; continue independent work within the
  authorized scope while waiting.

### Commit identity

Commits written by an agent must say so. Before committing, check that the repository-local
identity (never `--global`) is the bot's whose token you push with, so the Git host links the
commits to it and squash commits show the same name:

```bash
git config user.name "<bot's name>"   # e.g. "<username> office agent[bot]"
git config user.email "<bot's noreply address>"
```

On GitLab, `glab api user` shows the bot's `name` and `username`, and the address is
`<username>@noreply.<host>` (for example `project_…_bot_…@noreply.code.jlab.org`). Never commit
under a person's name or email. Name the agent and model in a trailer on every agent-written
commit, including follow-up and reviewer commits: keep the one your tool adds (such as
`Co-Authored-By`), or else add `Assisted-by: <agent>:<model>`.

### Commits

- Imperative summary line, then a body explaining what changed and why.
- Trailers come last, in one paragraph: the release trailers this file asks for, if any (such as
  `Changelog:`), then tool-required ones, with no blank line between them. Git reads trailers
  only from the last paragraph; check with
  `git log -1 --format=%B | git interpret-trailers --parse`.
- The squash commit message template uses the first commit's message: write it for `main`, and
  put release trailers on it only. Later commits, including a reviewer's, retain agent/model
  attribution trailers but omit release trailers. Keep the first message current for the whole
  change, preserving its attribution and adding any distinct agent/model attribution from later
  commits so the squash credits them too.
- Commit and push only when asked.

### Branches and merge requests

- Start each task on a new branch from an up-to-date `main`; target `main`. Work only in your
  own clone or worktree.
- Label every merge request `source::ai`, and make the person who reviews it both reviewer and
  assignee (ask for their username if you don't know it).
- Delete the source branch on merge. A person reviews and merges; never merge, approve, or
  enable auto-merge yourself.
- Before pushing to a merge request's branch, check that it is still open: commits pushed after
  it merged never reach `main`, so put them in a new merge request.
- To build on a merge request still in review, branch from its branch and target that branch,
  saying so in the description. Once the first merges, check that yours now targets `main`, and
  retarget it if not: GitHub retargets it only if the merged branch was deleted.
- Describe what changed, any deployment steps, and the checks you ran. Add a short Decisions
  part: what the person questioned or changed during the session, which alternatives were
  dropped and why, and any second opinion that changed something.
- End every agent-prepared merge request or pull request description with a plain-text
  `Prepared-by: <vendor> <agent>:<model identifier>` footer, such as
  `Prepared-by: OpenAI Codex:GPT-6`. Use the specific model identifier when known; otherwise
  use the known model family, omitting unknown variant or version details. Use `unavailable`
  only when neither is known; never guess. Preserve existing credits. For substantive
  description updates, append your own footer if that vendor, agent, and model are not already
  credited; routine metadata or formatting edits need no new footer. Keep equivalent
  tool-provided footer attribution that identifies the vendor, agent, and known model without
  adding an identical credit; if it is incomplete, keep it and add the `Prepared-by:` footer.
  Keep all attribution at the end. In a release merge request or pull request, put it under a
  separate heading after "Release notes", outside that section.
- After pushing, wait for the merge request's pipeline to finish before reporting it ready. Read
  failed jobs' logs and fix the cause; never skip or weaken a check to pass it. Report how it
  ended, with its link, and say if it failed for a reason outside your change. On GitLab,
  `glab mr view <number> -F json | jq '.head_pipeline | {sha, status, web_url}'` shows it; until
  its `sha` is the commit you pushed, your push's pipeline has not started, and
  `glab ci status --wait` would follow an older one.
- Name the issue a change is for in the commit body: `Fixes #<issue>`, or `Part of #<issue>` if
  some of it stays open. Work you were asked to do needs no issue.
- For another GitLab project's issue or merge request, prefer `project#123` or
  `project!456` with no space, when it resolves correctly from the current context. Check
  the rendered link's destination before publishing. Otherwise use the full namespace path
  or full URL; GitLab can display the URL as a reference. An explicit Markdown link can
  use a concise label. `project #123` and
  `project !456` link the bare number to the current project. Apply this to descriptions,
  comments, commit messages, and documentation.

### Issues

- For something outside your task (another project, code another agent is working on, or a
  change that needs a decision), don't fix it in passing: offer to open an issue, labeled
  `source::ai` and assigned to the person you work with, with what you found, the evidence, and a
  suggested fix.
- File issues as the bot with `source::ai`, and end their descriptions (including planning
  issues) with a plain-text `Prepared-by: <vendor> <agent>:<model identifier>` footer, such as
  `Prepared-by: OpenAI Codex:<model identifier>`,
  `Prepared-by: Anthropic Claude Code:<model identifier>`, or
  `Prepared-by: Google Gemini:<model identifier>`. Use the specific model identifier
  when known; otherwise use the known model family (for example `GPT-6`), omitting unknown
  variant or version details. Use `unavailable` only when neither is known; never guess.
  Preserve existing footers;
  for substantive analysis, proposed-change, or acceptance-criteria updates, append your own
  if that vendor, agent, and model are not already credited. Routine metadata or formatting
  edits need no new footer.
- When asked to plan work for other agents: check claims before filing, list draft issues before
  filing them, make each one self-contained with "Done when" and what not to do, say which can
  start now and which are blocked or share files, and ask before filing anything
  security-sensitive (file it confidential).

### Review

- When asked to address a review, reply in each thread with what you changed, and push content
  fixes as new commits; the reviewer resolves the threads. The author may reword only the first
  message when its description, release metadata, or attribution needs updating. Coordinate
  with anyone working on the branch, save the old tip, and keep the base, patches, and commit
  order unchanged. Rewording changes descendant IDs too: check `git range-diff` between the old
  and new series for only the intended message change, and `git diff <old-tip> HEAD` for no tree
  change. Push with `--force-with-lease=refs/heads/<branch>:<saved-remote-tip>`; stop if the lease
  fails. Report the old/new tips and message changes so the reviewer can compare patches.
- When asked to review a merge request, leave comments by default without asking which mode to
  use. Commit and push fixes only when asked; coordinate with the author before pushing if they
  are still working on the branch. Add one commit per recommendation on top of the author's
  (never rewrite theirs), retaining your agent/model attribution but omitting release trailers
  on follow-up commits. Then post one comment listing each commit, what you tested, that the
  author must pull, and any first-message update needed for the whole change or squash
  attribution. Never approve, merge, or resolve threads.
- When the person corrects you on something any agent here should know, propose adding it to
  this file's project rules, or better a check that catches it. Keep one developer's preferences
  out of it.

<!-- END coding-agents shared rules -->

## Project rules

- Required PR checks: `build` and `integration` from CI, and CodeQL. Integration takes
  about 5 minutes; `gh pr checks <number> --watch` waits.
- GitHub tool notes: use the common [Tool notes](https://code.jlab.org/acc/iac/docs/coding-agents/-/blob/v6.2.0/docs/git-workflow.md#tool-notes)
  for version-specific workarounds.
- Changelog eligibility/types and Breaking trailers: unused.
- Milestones: unused.
- Release trigger: `VERSION` on `main` creates the tag, GitHub release with the demo war,
  weblib on Maven Central, docs, demo Docker image, and deployment to JLab's test server.
  Release-only change: `VERSION` only. Never change `VERSION` unless asked.
- Release notes: PR description for the maintainer to add to the release; include upgrade steps.
  Versions: [Semantic Versioning](https://semver.org/), as in the README's Release section.
- Queue: the period's GitHub organization project. The agent cannot add items; report opened
  PRs and issues so the person can add them.
