# Getting Started

### Basic setup

* Install [Intellij Idea](https://www.jetbrains.com/idea/)
* Import project (build.gradle)
* Run
  * Open Gradle panel (View -> Tool Windows -> Gradle)
  * Tasks -> application -> VertxRun
  * (You might need to add some environment variables to make shits work)

### Workflow
Similarly to [gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow):
* `master` = `develop` branch
* something stable enough will be merged in `release` branch
* some new major features -> open issues, discuss (we have a Kanban board for this)
  * If OK and you think *X* should do this now, drag the issue to `Todo` tab, assign X (X is likely to be yourself)
  * Else put a `on-hold` tag
* some **minor** changes: pushing them directly to master is fine.

Note: auto-deploy will be triggered when `master` branch changes

### Unit test

Not needed (for now)

### Recommendation
* Linux > Window: Arch Linux, Fedora Workstation...
* Use shortcuts effectively, especially code format tool (Select code --> `Ctrl` + `Alt` + `L`)
* There is a built in git client in Intellij. **Gitkraken** is a great alternative (free for students) 
* Push code frequently, even if it does not work (to _some-feature_ branch only, don't push to master or hotfix if it does not work)
* Don't abuse premature optimization
* Write some questionable shits --> comment/add `// TODO blah blah` to those
* Don't hardcode system-dependent config or secret variable (use `System.getEnv(...)`)
* You can improve this guide
