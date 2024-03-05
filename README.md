March 5th 11:33Am

CURRENTLY WORKING ON:
- Hamburger popout menu for attendee dashboard (this menu will be extended to all users on almost all screens) 


What does this build Contain? 
- Basic Intro Screen
- Initial Attendee Profile Creation
- Storing Profile Creation data on Firebase
- Barbones Attendee Dashboard
- Drawer Menu within the Attendee Dashboard

Here are images that show what this build contains: 

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/2d88b80c-f689-4d43-bc3f-5bfd1143ca34)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/ef913f69-740c-45a4-9007-ba90a70ca121)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/15509be0-9906-458c-a521-b729df018654)'

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/bec80189-5c6d-4b8e-a1f8-e3c457f1f776)



 The only button on the first image with implementation is the Attendee button. Pressing this leads to the second image, which is the profile creation screen. Erquette informed us that the first time an attendee runs the app, they should be prompted to create an account. Filling the fields in on the second image and confirming uploads the data to Firebase, and then moves them over to the Attendee Dashobard. All of the UI here is really primitive, I spent little time on them, i was only focusing on trying to get activites and the logic implemented, we can just make it look nicer later. (we only have until friday 4pm :rage3: ) 

Note: Last lab session Erquette informed us that all users must be able to attend events. So administrators and organizers must be able to attend events. The way we should think about it is that organizers and administrators are just special attendees, so everyone needs to be an attendee. So based on this, after a user selects their role this profile creation page should appear to everyone, considering they need to be able to attend events. So for first app runtime i imagine these will be the first two screens a user will see 

