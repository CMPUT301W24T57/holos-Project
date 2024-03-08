What does this build Contain? 
- Basic Intro Screen
- Initial Attendee Profile Creation
- Storing Profile Creation data on Firebase
- Attendee Dashboard
- Drawer Menu within the Attendee Dashboard
- ViewAllEvents Activity
- EditProfile Activity (Not implemented with Firebase yet)
- QR Scanning Functionality (both Promo and Check-In)
- Promo QR codes send the user to the event details
- Check-in QR code send the user to a check-in activity where they can check-in to an event
- Basic administrator browsing
- Ability for attendee to view events they've signed up for

Here are images that show what this build contains:

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/2d88b80c-f689-4d43-bc3f-5bfd1143ca34)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/ef913f69-740c-45a4-9007-ba90a70ca121)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/8545856e-1160-43a4-9b9e-3cc3584b5dc3)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/14855a6c-6234-4d1e-bbe6-fb8b73d140b2)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/b2a6d01b-6dc8-4596-834f-5c86c267f2d4)

![image](https://github.com/CMPUT301W24T57/holos-Project/assets/82182216/ee95e7b0-ffeb-4082-841a-2386bb8e8dac)

 The only button on the first image with implementation is the Attendee button. Pressing this leads to the second image, which is the profile creation screen. Erquette informed us that the first time an attendee runs the app, they should be prompted to create an account. Filling the fields in on the second image and confirming uploads the data to Firebase, and then moves them over to the Attendee Dashobard. All of the UI here is really primitive, I spent little time on them, i was only focusing on trying to get activites and the logic implemented, we can just make it look nicer later. (we only have until friday 4pm :rage3: ) 

Note: Last lab session Erquette informed us that all users must be able to attend events. So administrators and organizers must be able to attend events. The way we should think about it is that organizers and administrators are just special attendees, so everyone needs to be an attendee. So based on this, after a user selects their role this profile creation page should appear to everyone, considering they need to be able to attend events. So for first app runtime i imagine these will be the first two screens a user will see 
