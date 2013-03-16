package aws.apps.androidDrawables.containers;

public class ResourceInfo {
	private final String mName;
	private final Integer mId;
	private final String mType;
	
	public ResourceInfo(Integer id, String name, String type){
		this.mName = name;
		this.mId = id;
		this.mType = type;
	}

	public ResourceInfo(String name, String type){
		this(null, name, type);
	}
	
	public String getName() {
		return mName;
	}

	public int getId() {
		if(mId == null){
			return 0;
		} else {
			return mId;
		}
	}

	public String getType() {
		return mType;
	}
}
