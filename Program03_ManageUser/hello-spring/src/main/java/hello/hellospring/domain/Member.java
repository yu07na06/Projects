package hello.hellospring.domain;

public class Member {

    private Long id; // 식별자(시스템이 정하는 id, 고객이 지정하는 id가 아님)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
