# Introduction #

Sure shipping patch files and merging them with fun (not) but
wouldn't it be great to use git?
Here is how (and you don't even have to mess with git-svn).

# Details #

First off I presume you already know the advantages of using git.
If you have tried to make use of git-svn and found it
confusing or just goofy then this is for you.

The problem can be reduced to "How can I use one working copy for two different revision control systems?" namely git and subversion.

Suppose you have an subversion working copy created using :
<pre>
svn checkout http://osmdroid.googlecode.com/svn/trunk/ osmdroid-read-only<br>
</pre>
That's cool but before you go any further humor me and make sure
the squeaky clean **osmdroid-read-only** directory lives in
an otherwise empty **osmdroid** directory.

Now you add your wonderful enhancement to the osmdroid code base.
You want to share your patch with someone else so you make a patch
file...
<pre>
svn diff > ~/cool-feature.diff<br>
</pre>
...and post it to the mailing list.
Someone with rights to the repository takes a look at your patch
and --joy of joys-- thinks it is great and adds it to the public repository.
But your sponsor does it wrong so you iterate.
Now your sponsor could make a branch but then that branch
will ultimately need to be merged.
Enough of that now consider the git way.

First a little preparation work.
Change to the **osmdroid** directory and run...
<pre>
git init<br>
</pre>
This creates the local git repository rooted at **osmdroid/.git**.
Now create a **osmdroid/.gitignore** file making sure it
contains **.svn/** on a line by itself.
This will cause git to not try to manage the magic **.svn** directories and their content.
Now from **osmdroid** add all the managed files...
<pre>
git add .gitignore<br>
# the following may get you too much unless you have a clean check out.<br>
# otherwise augmenting .gitignore or svn ls -R may be helpful<br>
git add osmdroid-read-only<br>
git commit -m "initial load from upstream"<br>
</pre>
Congratulations you now have a working copy that recognizes both **svn** and **git** commands.
So how does this help?

First of all once you have a sponsor, you and your sponsor can work out the
details without corrupting the public svn repository.
You can exchange patch files (git-format-patch) or if you
want to speed things up, you can
pull commits from each others git repositories.
Presuming the contributor and sponsor are using anonymous pull-only git servers...
<pre>
## (the ip addresses are for illustration only)<br>
# the contributor identifies his sponsor<br>
git remote sponsor git://192.168.1.31/osmdroid.git<br>
# the sponsor identifies his contributor<br>
git remote contrib-joe git://10.0.0.17/osmdroid.git<br>
# then, for example, the contributor can pull the sponsor's changes.<br>
git pull sponsor master<br>
</pre>
When both you and your sponsor are happy with the change your sponsor
only needs to say...
<pre>
svn commit -m "a way cool feature"<br>
</pre>
...to put it in the public repository without all the noise.