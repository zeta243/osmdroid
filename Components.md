# Deprecated #

This information is outdated, and is marked for deletion.

# Services #

The service API of osmdroid is not finished yet. There are several possibilities to achieve the separation of tile storage & retrieval and an app. Here is a comparison:

<table cellspacing='20'><tr><td>
<h2>Passing bitmap objects</h2>

The bitmap goes through the following steps until it is displayed:<br>
<br>
<b>Service:</b>
<ul><li>download or load from SD-card<br>
</li><li>decode bitmap format (JPG, PNG)<br>
</li><li>pass bitmap object</li></ul>

<b>Activity:</b>
<ul><li>get bitmap object from service<br>
</li><li>draw to screen</li></ul>

<b>Pro:</b>
<ul><li>Could be faster (SD-card needs not to be accessed in case of download)<br>
</li><li>Storage on SD-card is transparent (can be ZIP-files or database or whatever)</li></ul>

</td><td>
<h2>Passing file paths</h2>

The bitmap goes through the following steps until it is displayed:<br>
<br>
<b>Service:</b>
<ul><li>download<br>
</li><li>store to SD-card<br>
</li><li>pass file path</li></ul>

<b>Activity:</b>
<ul><li>get file path from service<br>
</li><li>load from SD-card<br>
</li><li>decode bitmap format (JPG, PNG)<br>
</li><li>draw to screen</li></ul>

<b>Pro:</b>
<ul><li>No need to pass large objects between service and activity<br>
</td></tr></table>