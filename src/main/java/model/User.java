package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
//    @Override
//    public boolean equals(Object obj) {
//    	if(this == obj)
//    		return true;
//    	if(obj == null)
//    		return false;
//    	if(getClass() != obj.getClass())
//    		return false;
//    	User user = (User) obj;
//    	if(this.userId == null || this.password == null)
//    		return false;
//    	return this.userId.equals(user.userId) && this.password.equals(user.password);
//    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
