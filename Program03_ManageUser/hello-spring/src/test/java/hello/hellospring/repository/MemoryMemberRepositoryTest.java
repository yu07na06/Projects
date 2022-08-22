package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MemoryMemberRepositoryTest {

    MemoryMemberRepository respository = new MemoryMemberRepository();

    // 테스트의 가장 중요한것은 순서에 상관없이 + 서로 의존관계 없이 설계가 되어야 합니다~

    @AfterEach // 콜백 함수 개념입니다~~ 각각의 메소드가 끝나면 AfterEach 어노테이션이 붙어있는 녀석이 실행됩니다!!
    public void afterEach(){
        respository.clearStore();
    }

    @Test
    public void save(){
        Member member = new Member();
        member.setName("spring"); // 이름 세팅

        respository.save(member);
        Member result = respository.findById(member.getId()).get();
//        System.out.println("result = "+ (result == member));
//        Assertions.assertEquals(member, result);
//        Assertions.assertEquals(member, null);
        assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByNmae(){
        Member member1 = new Member();
        member1.setName("spring1");
        respository.save(member1);

        // 시프트 + F6 누르면 이름 전체 변경가능
        Member member2 = new Member();
        member2.setName("spring2");
        respository.save(member2);

        Member result =respository.findByName("spring1").get();
        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll(){
        Member member1 = new Member();
        member1.setName("spring1");
        respository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        respository.save(member2);

        List<Member> result = respository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }




}
