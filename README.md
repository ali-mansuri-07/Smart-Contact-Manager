# Smart Contact Manager Application

The Smart Contact Manager is a responsive web application developed using HTML, CSS, JavaScript, Thymeleaf, Spring Boot, and MySQL. It provides users with a platform to manage their contacts securely.

## Key Features

- **User Authentication and Authorization**: Users can sign up and log in securely using Spring Security.
  
- **User Management**: Users can create, update, and view their profiles.
  
- **Contact Management**: Users can create, update, view, and delete their contacts.
  
- **Password Change**: Users can change their passwords using OTP through email.

## Technologies Used

- **Frontend**: HTML, CSS, JavaScript, Thymeleaf
  
- **Backend**: Spring Boot
  
- **Database**: MySQL
  
- **Security**: Spring Security
  
## Getting Started

1. **Clone the Repository**:
git clone https://github.com/ali-mansuri-07/Smart-Contact-Manager


2. **Database Configuration**:
- Configure the MySQL database settings in `application.properties`.

3. **Build and Run**:
- Build and run the application using Maven or your preferred IDE.

4. **Access the Application**:
- Open a web browser and navigate to `http://localhost:8080` to access the application.

## How to Use

1. **Sign Up**:
- Register as a new user by providing the required details.

2. **Log In**:
- Log in using your credentials to access the dashboard.

3. **Manage Contacts**:
- Create, update, view, and delete your contacts from the dashboard.

4. **Change Password**:
- Change your password securely using OTP through email.

5. **Forgot Password**:
- If you forget your password, you can request a password reset by providing your email address. An OTP will be sent to your email for verification.

## Additional Information

### Search Controller

The `SearchController` is responsible for handling search requests to find contacts based on the query provided by the user.

### Forgot Controller

The `ForgotController` manages the functionality related to resetting a user's password. It includes the process of sending an OTP to the user's email for verification and allowing the user to change their password.
