# Secure-Repository
The application is a secure repository for storing confidential documents. 
# Descritpion 
Users log into the system by entering a digital certificate, which they receive when creating an account, as well as by entering a username and password. 
Once a user logs in, they can upload new documents and download existing ones.
The security of each document is ensured by splitting into N>=4 segments, each segment is digitally signed using the SHA 256 algorithm and the user private RSA key.
After the digital signature, each segment is encrypted using the AES algorithm, the key is randomly generated with a size of 256 bits. 
The key is then encrypted using the user public RSA key. In this way, the secrecy and integrity of each segment is ensured. 
I used OpenSSL to work with digital certificates and crypto-algorithms.
# Screenshots
![image](https://github.com/MarioPerac/Secure-Repository/blob/main/ProjectTask/screenshots/Screenshot%201.png?raw=true)
![image](https://github.com/MarioPerac/Secure-Repository/blob/main/ProjectTask/screenshots/Screenshot%202.png?raw=true)
![image](https://github.com/MarioPerac/Secure-Repository/blob/main/ProjectTask/screenshots/Screenshot%203.png?raw=true)
