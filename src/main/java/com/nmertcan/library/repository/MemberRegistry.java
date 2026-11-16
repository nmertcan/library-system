package com.nmertcan.library.repository;

import com.nmertcan.library.domain.Member;
import com.nmertcan.library.exception.LibraryException;
import java.util.*;

public final class MemberRegistry {
    private final Map<String, Member> members = new LinkedHashMap<>();
    public void add(Member member) {
        Objects.requireNonNull(member);
        if (members.putIfAbsent(member.id(), member) != null) throw new LibraryException("Üye ID zaten kayıtlı: " + member.id());
    }
    public Member get(String id) {
        Member member = members.get(id);
        if (member == null) throw new LibraryException("Üye bulunamadı: " + id);
        return member;
    }
    public List<Member> all() { return List.copyOf(members.values()); }
}
