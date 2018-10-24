# ImageLoader

***Developing environment:***

* Android Studio 3.2
* Build #AI-181.5540.7.32.5014246, built on September 17, 2018
* JRE: 1.8.0_172-b11 x86
* JVM: Java HotSpot(TM) Server VM by Oracle Corporation
* Windows 7 6.1

***Description of the project:***

**Application A:**
* Application A contents of Test tab to enter the link of the image and History tab with the list of opened image links; 
* After pressing Ok button Application B is opened;
* Application A does not make any changes in the link list (insert/update/delete);
* Action bar of the History tab contains two buttons to sort links after the status or open time;
* Background color of every list item depends on the status of the link (green for status 1, red for status 2, grey for status 3);
* Pressing on the link in the list openes Application B.

**Application B:**
* If Application B is opened after pressing Ok button on the Test tab it shows the image and saves the link to the database of Application A with fields: link, status (1 - downloaded, 2 - error, 3 - undefined) and open time;
* If application B is opened from History tab it shows the image and if the status of image was 1, this link is deleted from the database after 15 seconds and the message is shown even if the application is closed;
* If red or grey link is opened, its status is updated in the case it changes;
* If Application B is opened from Launcher, the message "Application could not work independently and will be closed in n seconds" is shown. The timer is count down 10 seconds and shows remaining time in the message. After 10 s Application B is closed.

