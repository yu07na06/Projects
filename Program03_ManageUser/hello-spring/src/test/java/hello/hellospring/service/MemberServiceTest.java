package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void 회원가입() {
        //given (제공)
        Member member = new Member();
        member.setName("hello");

        //when (조건)
        Long saveId = memberService.join(member);

        //then (결과)
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);

        //then
        /* 예외가 터져야하는 경우를 검증 */
        // 방법[1] try-catch걸기
        try {
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }

        // 방법[2] assertThrows 사용
        assertThrows(IllegalStateException.class, ()-> memberService.join(member2));

        // 방법[3] assertThrows의 메세지 검증
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}



//package hello.hellospring.service;
//
//import hello.hellospring.domain.Member;
//import hello.hellospring.repository.MemoryMemberRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//class MemberServiceTest {
//    // 테스트 쉽게 만드는 방법 ctrl + shift + T
//    // 테스트 메소드 이름은 한글로 해도 됨!! 아무 문제 없음! kikiki!
//
//    MemberService memberService;
//    MemoryMemberRepository m;
//
//    @BeforeEach
//    public void beforeEach(){
//        m = new MemoryMemberRepository();
//        memberService = new MemberService(m);
//    }
//
//    @AfterEach
//    void 초기화(){
//        m.clearStore();
//    }
//
//    @Test
//    void 회원가입() {
//        // given  뭔가가 주어졌는데
//        Member member= new Member();
//        member.setName("유후후나");
//
//        // when    이런 조건에
//        Long saveId = memberService.join(member);
//
//        // then    이런 결과가 나와야해
//        Member findMember = memberService.findeOne(saveId).get();
//        assertThat(member.getName()).isEqualTo(findMember.getName());
//    }
//
//    @Test
//    public void 회원가입폭파시키기(){
//        // given
//        Member member1= new Member();
//        member1.setName("유후후나");
//
//        Member member2= new Member();
//        member2.setName("유후후나");
//
//        // when
//        memberService.join(member1);
//        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
//
//        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
//        // then
//
//
//
//    }
//
//    @Test
//    void findMembers() {
//    }
//
//    @Test
//    void findeOne() {
//    }
//}