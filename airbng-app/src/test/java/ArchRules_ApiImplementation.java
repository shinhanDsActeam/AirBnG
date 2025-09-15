import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// API 인터페이스 구현 위치 규칙
// api.* 패키지 하위 인터페이스는 해당 모듈에서만 구현 가능
@AnalyzeClasses(packages = "com.airbng")
class ArchRules_ApiImplementation {

    // -------- 공용 predicate: 특정 패키지 하위 "인터페이스"만 매칭 --------
    private static DescribedPredicate<JavaClass> apiInterfacesIn(String apiPackagePrefix) {
        return new DescribedPredicate<>("interfaces in " + apiPackagePrefix) {
            @Override
            public boolean test(JavaClass input) {
                return input.isInterface() && input.getPackageName().startsWith(apiPackagePrefix);
            }
        };
    }

    // api.admin.* 인터페이스는 chat 모듈에서만 구현 가능
    private static final DescribedPredicate<JavaClass> ADMIN_API_IFACES =
            apiInterfacesIn("com.airbng.api.admin");

    @ArchTest
    static final ArchRule ADMIN_API_IMPLEMENTED_ONLY_IN_ADMIN =
            classes().that().implement(ADMIN_API_IFACES)
                    .should().resideInAPackage("..admin..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule NON_ADMIN_MUST_NOT_IMPLEMENT_ADMIN_API =
            noClasses().that().resideOutsideOfPackages("..admin..")
                    .should().implement(ADMIN_API_IFACES)
                    .allowEmptyShould(true);

    // api.consumer.* 인터페이스는 chat 모듈에서만 구현 가능
    private static final DescribedPredicate<JavaClass> CONSUMER_API_IFACES =
            apiInterfacesIn("com.airbng.api.consumer");

    @ArchTest
    static final ArchRule CONSUMER_API_IMPLEMENTED_ONLY_IN_CONSUMER =
            classes().that().implement(CONSUMER_API_IFACES)
                    .should().resideInAPackage("..consumer..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule NON_CONSUMER_MUST_NOT_IMPLEMENT_CONSUMER_API =
            noClasses().that().resideOutsideOfPackages("..consumer..")
                    .should().implement(CONSUMER_API_IFACES)
                    .allowEmptyShould(true);

    // api.chat.* 인터페이스는 chat 모듈에서만 구현 가능
    private static final DescribedPredicate<JavaClass> CHAT_API_IFACES =
            apiInterfacesIn("com.airbng.api.chat");

    @ArchTest
    static final ArchRule CHAT_API_IMPLEMENTED_ONLY_IN_CHAT =
            classes().that().implement(CHAT_API_IFACES)
                    .should().resideInAPackage("..chat..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule NON_CHAT_MUST_NOT_IMPLEMENT_CHAT_API =
            noClasses().that().resideOutsideOfPackages("..chat..")
                    .should().implement(CHAT_API_IFACES)
                    .allowEmptyShould(true);

    // api.pay.* 인터페이스는 chat 모듈에서만 구현 가능
    private static final DescribedPredicate<JavaClass> PAY_API_IFACES =
            apiInterfacesIn("com.airbng.api.pay");

    @ArchTest
    static final ArchRule PAY_API_IMPLEMENTED_ONLY_IN_PAY =
            classes().that().implement(PAY_API_IFACES)
                    .should().resideInAPackage("..pay..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule NON_PAY_MUST_NOT_IMPLEMENT_PAY_API =
            noClasses().that().resideOutsideOfPackages("..pay..")
                    .should().implement(PAY_API_IFACES)
                    .allowEmptyShould(true);
}
