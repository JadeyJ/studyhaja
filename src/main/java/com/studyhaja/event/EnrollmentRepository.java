package com.studyhaja.event;

import com.studyhaja.domain.Account;
import com.studyhaja.domain.Enrollment;
import com.studyhaja.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
