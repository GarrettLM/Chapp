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

	public static AccountMetaData[] buildList(String listString) {
		String[] temp = listString.split(";");
		AccountMetaData[] list = new AccountMetaData[temp.length / 2];
		for (int i = 0; i < temp.length; i += 2) {
			list[i/2] = new AccountMetaData(new Integer(temp[i]), temp[i+1]);
			accountRegistry.put(list[i/2].getUID(), list[i/2]);
		}
		return list;
	}

	public static void register(AccountMetaData data) {
		accountRegistry.put(data.getUID(), data);
	}

	public static AccountMetaData getMetaData(Integer uID) {
		return accountRegistry.get(uID);
	}
}
