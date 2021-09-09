package com.studyhaja.domain;

import com.studyhaja.account.UserAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NamedEntityGraph(name = "Event.withEnrollments", attributeNodes = {
        @NamedAttributeNode("enrollments")})
@Entity
@Getter
@Setter @EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime cretedDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = true)
    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(value = EnumType.STRING)
    private EventType eventType;

    public boolean canEnrollFor(UserAccount userAccount) {
        return isNotClosed() && !isAlreadyEnrolled(userAccount);
    }

    public boolean canNotEnrollFor(UserAccount userAccount) {
        return isNotClosed() && isAlreadyEnrolled(userAccount);
    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for( Enrollment e : this.enrollments ) {
            if(e.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for( Enrollment e : this.enrollments ) {
            if(e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }
        return false;
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType == EventType.FCFS && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    public void acceptNextWaitingEnrollment() {
        if(this.isAbleToAcceptWaitingEnrollment()){
            Enrollment enrollmentToAccept = this.getTheFirstWaitingEnrollment();
            if(enrollmentToAccept != null) {
                enrollmentToAccept.setAccepted(true);
            }
        }
    }

    private Enrollment getTheFirstWaitingEnrollment() {
        for(Enrollment e : this.enrollments) {
            if( !e.isAccepted() ) {
                return e;
            }
        }
        return null;
    }

    public boolean canAccept(Enrollment enrollment){
        return this.eventType == EventType.FCFS
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.FCFS
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    public void acceptWaitingList() {
        if(this.isAbleToAcceptWaitingEnrollment()) {
            List<Enrollment> waitingList = getWaitingList();
            int numberToAccept = (int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(), waitingList.size());
            waitingList.subList(0, numberToAccept).forEach(enrollment -> enrollment.setAccepted(true));
        }
    }

    private List<Enrollment> getWaitingList() {
        return this.enrollments.stream()
                .filter(enrollment -> !enrollment.isAccepted())
                .collect(Collectors.toList());
    }
}
