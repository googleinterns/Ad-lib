# What's in this directory?
The directory contains one Python script, used to simulate a real-world application in your repo. Now we suppose this script is the most important application in this repo and we need to make sure it run all the time. The purpose is for the users to learn to use bisection tools to find out when the script stops to work.

# What is the application doing?
It counts the number of lines of comments in the whole repo, by only considering supported program types: java, js and html. When the number of lines exceeds the limit (by default it's 1000, but user can override it with an argument. See `./bisection/application.py --help` for how-to.

# What do you need to do?
Execute the binary with `./bisection/application.py` occasionally (don't do it after every commit, as you might know the answer), to see if it prints the number of comments in the repo without any errors. If not, you can relax: The "Application" is working!

When you see `RuntimeError: Oh no!!!! You have xx lines of comments, exceeding the threshold of xx`, it means the application stops to work. This is the time you need to start to read documents on how to use `git bisection` to find out the culprit commit. https://git-scm.com/docs/git-bisect is a good start.
