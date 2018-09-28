
public class AbstractEntity {

    private Integer id;

    protected AbstractEntity(Integer id) {
        this.id = id;
    }

    protected AbstractEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

