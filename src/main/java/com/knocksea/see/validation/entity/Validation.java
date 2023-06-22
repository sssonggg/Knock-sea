package com.knocksea.see.validation.entity;

import com.knocksea.see.user.entity.User;
import lombok.*;

import javax.persistence.*;

import static com.knocksea.see.validation.entity.ValidationStatus.WAIT;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "eduId")
@NoArgsConstructor
@AllArgsConstructor
@Builder @Entity
@Table(name="sea_validation")
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length =10)
    private Long validationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidationType validationType; //SHIP/SPOT

    @Column(length = 50)
    private String validationShipRegi; //선박등록증 번호 //배 등록때만 사용

    @Column(length = 50)
    private String validationShipLicense; //선박면허증 번호 //배 등록때만 사용

    @Column(length = 50)
    private String validationBusinessRegi; //사업자 등록증 번호 //낚시터 등록때만 사용

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ValidationStatus validationStatus=WAIT; //YES/NO/WAIT


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
