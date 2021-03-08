import java.util.Hashtable;

public class AccountMetaData {
	private static Hashtable<Integer, AccountMetaData> accountRegistry = new Hashtable<Integer, AccountMetaData>();

	private Integer uID;
	private String username;

	public AccountMetaData(Integer uID, String username) {
		this.uID = uID;
		this.username = username;
	}

	public Integer getUID() { return uID; }
	public String getUsername() { return username; }

	public static void register(AccountMetaData data) {
		accountRegistry.put(data.getUID(), data);
	}

	public static AccountMetaData getMetaData(Integer uID) {
		return accountRegistry.get(uID);
	}
}
