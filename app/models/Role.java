package models;

/**
 * @author cmniu
 *
 */
public enum Role {
	NORMAL("normal", 0), CASHIER("cashier", 1), MERCHANT("merchant", 2), ADMIN("admin", 3);
	private String name;  
    private int index;  
    
    private Role(String name, int index) {
        this.name = name;  
        this.index = index;  
    }  
    
    public static String getName(int index) {  
        for (Role a : Role.values()) {  
            if (a.getIndex() == index) {  
                return a.name;  
            }  
        }  
        return null;  
    }  
    
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    }  
}
