package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository{
    // 실무에서는 동시성 문제로 공유되는 변수일때, ConcurrentHashMap을 써야한다는 점!
    private static Map<Long, Member> store = new HashMap<>();
    // 실무에서는 동시성 문제로 AtomicLong같은걸 써야한다는 점!
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence); // id는 시스템에서 설정해줌
        store.put(member.getId(), member); // 시스템에서 정해준 id로 store에 저장
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        // id가 없으면 null을 반환한다.
        // Optional로 감싸서 반환하면, 클라리언트에서 다른 동작을 할 수 있다.
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream() // loop로 돌림
                .filter(member -> member.getName().equals(name)) // 파라미터 name과 같으면 반환
                . findAny(); // 하나라도 찾으면, 걔를 그냥 바로 반환
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear();
    }


}