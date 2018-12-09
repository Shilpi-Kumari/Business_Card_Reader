# Business Card Directory

- Project 2: Business Card Directory<br>
- University Name: http://www.sjsu.edu/<br>
- Course: [Cloud Technologies](http://info.sjsu.edu/web-dbgen/catalog/courses/CMPE281.html)<br>
- Professor: [Sanjay Garje](https://www.linkedin.com/in/sanjaygarje/)<br>
- ISA: [Anushri Srinath Aithal](https://www.linkedin.com/in/anushri-aithal/)<br>
- Student: [Shilpi Kumari](www.linkedin.com/in/shilpi-kumari-58764a64)
- Student: [Sumanth Ravipati](https://www.linkedin.com/in/sumanth-ravipati)
- Student: [Harshit Nagpal](https://www.linkedin.com/in/harshit-nagpal)

## Project Introduction<br>

### Problem Statement
We get lots of visiting card from retail people, sales people, tech conferences, restaurants and hospitals etc. we store the physical ones for some time and then lose and it becomes unmanageable also. we can’t carry all of them with us always to use it. What if there is one platform which will manage the visiting cards on behalf of us and will be always available online. There will be no need to keep the hard copy and also visiting card information will be secure. This application will save the images and details of the visiting cards on cloud and will be available always once uploaded through this application.

### Project Solution
This will be a 3-tier web application using AWS resources for managing the visiting cards. We can upload the visiting card image with this app whenever we get them. It will get stored in the AWS S3 & AWS RDS with all details - name, designation, phone and email. So, it can be anyone’s personal contact list for different business services. Application user will also be able to rate the people’ services and put some comments as well based on their service for future reference and suggesting the contacts to friends. Amazon Lex will be used to receive the voice input and the required output will be given by Amazon Polly to the end users. Also, two people can share their contact list as well.

### Features List:
- Home Page to give the options- Signup and Login for Users and Login with social media accounts like Google, Facebook, LinkedIn.
- Sign up form for new user to register for our website application by creating an account.
- Login Page which allows only the authorized users to login which means the users of our application or the users with existing Google, Facebook, LinkedIn accounts.
- Dashboard Screen that displays welcome message for users and display their First Name and Last Name.
- Allows authorized users to upload the new card or download their existing cards from S3 via Cloud Front.
- List Cards page to Displays all the Cards uploaded by the user previously with all the details and offers them to download or delete the cards.
- Allows the user to search the particular card by using Name and Organization Filter.
- Allows the user to access the information of the cards in List Cards page which they have previously stored using Voice recognition.
- Allows the users to add the details of the card uploaded in the application to the User’s Google Contacts for later access.
- Allows the users to refer the cards to the another user by sharing their individual card details and the user will get an email of the referred card with all the details.
- Allows the user to search about the information of particular organization like- reviews, ratings and services offered by using Voice recognition. 
- Allows the user to request or schedule a meeting with the organization person using Chatbot Service of our Web Application. - This will send the notification to the organization person and the user about the requested service.
User can Logout using the logout button.

### Architecture Diagram:
### AWS Services Used:
1. Elastic Beanstalk: To provide the deployment environment for our application to run. Enables EC2 instances. Just need to configure the Capacity settings for auto scaling group and load balancing. Application uses two elastic beanstalk environment to support CI/CD Integration with our application- one for Production and other for Development .
2. Code Pipeline: To provide the CI/CD Integration with our application, we used two Code Pipelines. One for Development Environment and other for Production Environment. Manual Approval is required in the Production Environment to reflect the changes.
3. S3: It is used to upload and maintain the Business card details. 
4. S3 Transfer Acceleration: S3 bucket associated with the application to store cards is enabled with Transfer Acceleration to enable faster and secure transfer of the cards.
5. Standard Infrequent Access (IA): Lifecycle policies are created on the associated S3 Bucket to move Cards from Standard to IA after 75 days.
6. Amazon Glacier: Lifecycle policies are enabled on S3 Bucket to move the stored Business Cards from IA to Amazon Glacier after 365 days.
7. CloudFront: Used for enabling the user to Download the Card stored previously using  CloudFront. The minimum TTL for CloudFront is setup as 40 seconds.
8. AutoScaling Group: This configuration is to achieve highly available and highly scalable solution. I have set the desired > instance 1 and maximum 2 for my application. This Auto Scale group help the application in adjusting the EC2 instances to meet the changing condition of traffic. It will be also maintaining the desired capacity by checking health of EC2 instance periodically.
9. Elastic Load Balancer: Classic load balancer is used to distribute the load on all the EC2 instances. It points to auto scale group to handle the load. One alarm is set on load balancer if minimum number of healthy states is less than 1.<br>
10. RDS: MySQL instance is created to maintain user profile, business card details etc .<br>
11. AWS Rekognition – This feature is used to get the text from the Business Card image that we are uploading. Response will be in JSON format with complete data to text in that card.
12. AWS Comprehend – Natural Language Processing and Text Analytics feature provided by AWS. JSON response that we get from AWS Rekognition will be passed into AWS Comprehend to get the Business Card Contact and the Organization information.
13. Polly- Used to speak out the responses loud to the end user.
14. Lex – Used the Lex Chatbot Service to enable the user for scheduling a service or meeting with the organization person.
15. AWS Cognito-  Used Amazon Cognito to authenticate the user accessing our web application to interact with the Lex Chatbot to schedule a meeting.
16. Lambda: A nodejs program to perform the business Logic for scheduling a meeting with the requested person by sending them emails after getting triggered by AWS Lex.
17. SES: Used AWS Simple Email Service to send the notification to the employee who has the requested service/meeting and also sends the confirmation email to the user who has requested. Also, used the same for referring feature of our web application so that users can share their individual cards to another user.
18. CloudWatch: Cloud watch is used to monitor the capability and metrics of the RDS Instance. Cloud Watch Alarm can be created to inform the admin through SNS when the Instances reaches in the specified state.
19. SNS: Configured to send email to all the subscribers for the topic. Created two topics - one for Code Pipeline to push notifications for approval request.
20. CloudFormation- Used to automate the things in the console and to increase the security. We have configured Cloud formation tempelate in yaml format to automate the components -
1> S3 bucket used to store cards                          
2> Lambda function with the associated IAM role
3> Elasticbeanstalk environment and application                            
4> Auto Scaling group

21. Cloud Trail –  Used to keep track of the activities and the API accessed for our account.
22.Route53: Domain Name Server that resolves IP address for the application domain www.sumanthravipati-sjsu.online.
23. AWS Certificate Manger: Used to issue the TSL/SSL Certificate for our domain to enable the https request to provide the encryption of the data in transit.

### Pre-requisites Set Up<br>
1) Download Spring Tool Suite 4 as IDE
2) Create Amazon Web Services account
3) Create a database in AWS MYSQL
4) Install MYSQLworkbench
5) Create S3 bucket in AWS
6) Create a IAM role and place AWS AccessKey and password securely

### Deplyment Instruction<br>
1) Clone the project URL from Github
2) Import the project in STS.
3) Change the application properties in  src/main/resources and save
4) Right click on project folder and Run as maven clean
5) Right click on project folder and Run as maven install
6) Right Click on project folder and Run as Spring Boot App
7) Open browser and provide https://localhost:portnumber
### Sample Screenshots:

- Login Page
<img width="1438" alt="screen shot 2018-12-08 at 7 59 56 pm" src="https://user-images.githubusercontent.com/42687329/49693343-b9d5ef80-fb24-11e8-9f2c-3e0ba8125546.png">

- Home Page:
<img width="1440" alt="screen shot 2018-12-08 at 8 01 00 pm" src="https://user-images.githubusercontent.com/42687329/49693349-ef7ad880-fb24-11e8-9b8c-7bf80f19351e.png">

- Upload Card:

- Search Card:

-List of Cards:

- Chatbot to schedule a meeting:
<img width="1338" alt="1" src="https://user-images.githubusercontent.com/42687329/49693366-59937d80-fb25-11e8-9889-a70e10e87941.png">

