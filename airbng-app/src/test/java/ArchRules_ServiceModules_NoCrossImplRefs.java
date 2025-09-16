import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// 서비스 모듈 간 구현체 참조 금지 규칙
@AnalyzeClasses(packages = "com.airbng")
public class ArchRules_ServiceModules_NoCrossImplRefs {

    // admin → (consumer/chat/pay) 구현 참조 금지
    @ArchTest
    static final ArchRule ADMIN_MUST_NOT_DEPEND_ON_OTHER_IMPLS =
            noClasses().that().resideInAPackage("..admin..")
                    .should().accessClassesThat()
                    .resideInAnyPackage("..consumer..", "..chat..", "..pay..")
                    .andShould().onlyBeAccessed().byAnyPackage("..api..")
                    .allowEmptyShould(true);

    // consumer → (admin/chat/pay) 구현 참조 금지
    @ArchTest
    static final ArchRule CONSUMER_MUST_NOT_DEPEND_ON_OTHER_IMPLS =
            noClasses().that().resideInAPackage("..consumer..")
                    .should().accessClassesThat()
                    .resideInAnyPackage("..admin..", "..chat..", "..pay..")
                    .andShould().onlyBeAccessed().byAnyPackage("..api..")
                    .allowEmptyShould(true);


    // chat → (admin/consumer/pay) 구현 참조 금지
    @ArchTest
    static final ArchRule CHAT_MUST_NOT_DEPEND_ON_OTHER_IMPLS =
            noClasses().that().resideInAPackage("..chat..")
                    .should().accessClassesThat()
                    .resideInAnyPackage("..admin..", "..consumer..", "..pay..")
                    .andShould().onlyBeAccessed().byAnyPackage("..api..")
                    .allowEmptyShould(true);


    // pay → (admin/consumer/chat) 구현 참조 금지
    @ArchTest
    static final ArchRule PAY_MUST_NOT_DEPEND_ON_OTHER_IMPLS =
            noClasses().that().resideInAPackage("..pay..")
                    .should().accessClassesThat()
                    .resideInAnyPackage("..admin..", "..consumer..", "..chat..")
                    .andShould().onlyBeAccessed().byAnyPackage("..api..")
                    .allowEmptyShould(true);

}
