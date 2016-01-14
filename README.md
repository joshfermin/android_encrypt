# webroot encrypt
Android application that gets a request from a server and then encrypts the specified directory on the SD card.
Also will display files and folders from the SD Card, and will allow you to encrypt/decrypt files from the app itself.

### Completed:
Client
* Client chooses folders to encrypt and sends over to the server
* Client does the encryption on the device after sending folders over to server
  * Encrypted files are denoted with a ".encrypted" ending ( this can be changed later )
  * Encryption is done by compressing the folder and then encrypting the zip. Decryption is done the same (decrypting and then unzipping).
* Client can also decrypt on the device. Just choose folders that are 

Server
* Recieves folders from Client in string form. 
* Gives Client acknowledgement that it has been recieved.

### To Do:
* Encryption does not get rid of the old files, i.e. unencrypted file and zipped
* Salt is the same for every user
* Server does not keep a record of the folders the user wants to keep encrypted.
* No web service to actually send a message to the device.
