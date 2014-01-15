package interface_module;

public class RowItem {
    private int imageId;
    private String title;
    private String author;
    private String year;
    private String _id;
    public RowItem(int imageId, String title, String desc,String year,String _id) {
        this.imageId = imageId;
        this.title = title;
        this.author = desc;
        this.year = year;
        this._id = _id;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return author;
    }
    public void setDesc(String desc) {
        this.author = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + author;
    }
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}