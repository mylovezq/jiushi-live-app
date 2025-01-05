package top.mylove7.live.living.provider.room.dao.po;

import cn.hutool.core.util.RandomUtil;
import com.github.javafaker.Faker;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@Document("mycoll")
public class DoctorInfo {

    @Id
    private Long id;
    @Field
    private String name;
    @Field
    private String gender;
    @Field
    private Integer age;
    @Field
    private String specialty;
    @Field
    private String department;
    @Field
    private String hospital;
    @Field
    private String medicalLicenseNumber;
    @Field
    private String registrationNumber;
    @Field
    private String title;
    @Field
    private String biography;
    @Field
    private String profilePictureUrl;
    @Field
    private String email;
    @Field
    private String website;
    @Field
    private String socialMediaLinks;
    @Field
    private String languagesSpoken;
    @Field
    private String availability;
    @Field
    private String patientReviews;
    @Field
    private Double averageRating;
    @Field
    private Integer yearsOfExperience;
    @Field
    private String certifications;
    @Field
    private String researchInterests;
    @Field
    private String publications;
    @Field
    private String awardsAndHonors;
    @Field
    private String professionalMemberships;
    @Field
    private AddressInfo addressInfo;
    @Field
    private ContactInfo contactInfo;
    @Field
    private EducationBackground educationBackground;
    @Field
    private LocalDateTime createTime;
    @Field
    private LocalDateTime updateTime;
    @Field
    private List<StudentDto> students;

    public DoctorInfo() {
    }

    public DoctorInfo(Long id,Faker faker,Random random) {

        this.id = id;
        this.name = faker.name().fullName();
        this.gender = random.nextBoolean() ? "Male" : "Female";
        this.age = faker.number().numberBetween(20, 80);
        this.specialty = faker.medical().diseaseName();
        this.department = faker.medical().hospitalName();
        this.hospital = faker.medical().hospitalName();
        this.medicalLicenseNumber = faker.code().ean8();
        this.registrationNumber = faker.code().ean13();
        this.title = faker.medical().symptoms();
        this.biography = faker.lorem().paragraph(3);
        this.profilePictureUrl = "https://via.placeholder.com/150";
        this.email = faker.internet().emailAddress();
        this.website = faker.internet().url();
        this.socialMediaLinks = faker.internet().url() + ", " + faker.internet().url();
        this.languagesSpoken = "English, " + faker.address().countryCode();
        this.availability = "Monday to Friday, 9 AM - 5 PM";
        this.patientReviews = faker.lorem().sentence(5);
        this.averageRating = faker.number().randomDouble(1, 3, 7);
        this.yearsOfExperience = faker.number().numberBetween(5, 30);
        this.certifications = faker.medical().symptoms() + " Certification";
        this.researchInterests = faker.medical().symptoms() + " Research";
        this.publications = faker.book().title() + " in " + faker.medical().medicineName();
        this.awardsAndHonors = faker.medical().symptoms() + " Award";
        this.professionalMemberships = faker.medical().symptoms() + " Association";
        this.createTime = generateRandomLocalDateTime();
        this.updateTime = generateRandomLocalDateTime();

        this.addressInfo = new AddressInfo(
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode(),
                faker.address().country()
        );

        this.contactInfo = new ContactInfo(
                faker.phoneNumber().phoneNumber(),
                faker.phoneNumber().cellPhone(),
                faker.name().fullName(),
                faker.phoneNumber().phoneNumber()
        );

        this.educationBackground = new EducationBackground(
                faker.educator().secondarySchool(),
                faker.educator().university(),
                faker.number().numberBetween(1980, 2000),
                faker.medical().hospitalName() + " Residency",
                faker.medical().symptoms() + " Fellowship"
        );

        ArrayList<StudentDto> objects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            StudentDto studentDto = new StudentDto();
            studentDto.setId(RandomUtil.randomLong(20, 999999));
            studentDto.setName(faker.name().fullName());
            studentDto.setPhone(faker.phoneNumber().phoneNumber());
            objects.add(studentDto);
        }
        this.students = objects;
    }

    @Data
    public static class AddressInfo {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;

        public AddressInfo(String streetAddress, String city, String state, String postalCode, String country) {
            this.streetAddress = streetAddress;
            this.city = city;
            this.state = state;
            this.postalCode = postalCode;
            this.country = country;
        }
    }

    @Data
    public static class ContactInfo {
        private String phoneNumber;
        private String mobileNumber;
        private String emergencyContactName;
        private String emergencyContactPhoneNumber;

        public ContactInfo(String phoneNumber, String mobileNumber, String emergencyContactName, String emergencyContactPhoneNumber) {
            this.phoneNumber = phoneNumber;
            this.mobileNumber = mobileNumber;
            this.emergencyContactName = emergencyContactName;
            this.emergencyContactPhoneNumber = emergencyContactPhoneNumber;
        }
    }

    @Data
    public static class EducationBackground {
        private String degree;
        private String university;
        private int graduationYear;
        private String residencyProgram;
        private String fellowshipProgram;

        public EducationBackground(String degree, String university, int graduationYear, String residencyProgram, String fellowshipProgram) {
            this.degree = degree;
            this.university = university;
            this.graduationYear = graduationYear;
            this.residencyProgram = residencyProgram;
            this.fellowshipProgram = fellowshipProgram;
        }
    }

    public static void main(String[] args) {
        DoctorInfo doctor = new DoctorInfo();
        System.out.println(doctor);
    }

    public static LocalDateTime generateRandomLocalDateTime() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 定义时间范围（例如，前后一年）
        long daysRange = 365L;

        // 生成随机的天数偏移量
        long randomDaysOffset = RandomUtil.randomLong(-daysRange, daysRange);

        // 生成随机的小时偏移量
        long randomHoursOffset = RandomUtil.randomLong(0, 24);

        // 生成随机的分钟偏移量
        long randomMinutesOffset = RandomUtil.randomLong(0, 60);

        // 生成随机的秒偏移量
        long randomSecondsOffset = RandomUtil.randomLong(0, 60);

        // 应用偏移量生成随机的 LocalDateTime
        LocalDateTime randomDateTime = now
                .plusDays(randomDaysOffset)
                .plusHours(randomHoursOffset)
                .plusMinutes(randomMinutesOffset)
                .plusSeconds(randomSecondsOffset);

        return randomDateTime;
    }

    @Data
    private class StudentDto {
        private Long id;
        private String name;

        private String phone;
    }
}
