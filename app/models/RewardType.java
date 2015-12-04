package models;

public enum RewardType {
	FREE("free", 0), DISCOUNT("discount", 1), COMMISSION("commission", 2);
    private String name;  
    private int index;  
    
    private RewardType(String name, int index) {
        this.name = name;  
        this.index = index;  
    }  
    
    public static String getName(int index) {
        for (RewardType a : RewardType.values()) {  
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

