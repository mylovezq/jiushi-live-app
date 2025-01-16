package top.mylove7.live.living.provider.room.dao.po;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.javafaker.Faker;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@Document(indexName = "doctor_info")
public class EsDoctorInfo {

    private static final int MIN_UNICODE = 0x4E00;
    private static final int MAX_UNICODE = 0x9FA5;
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

    @Field(type = FieldType.Nested)
    private AddressInfo addressInfo;

    @Field(type = FieldType.Nested)
    private ContactInfo contactInfo;

    @Field(type = FieldType.Nested)
    private EducationBackground educationBackground;

    @Field(type = FieldType.Date,format = DateFormat.date_hour_minute_second_millis )
    @ValueConverter(CentralstoreLocalDateTimeConverter.class)
    private LocalDateTime createTime;

    @Field(type = FieldType.Date,format = DateFormat.date_hour_minute_second_millis )
    @ValueConverter(CentralstoreLocalDateTimeConverter.class)
    private LocalDateTime updateTime;

    @Field(type = FieldType.Nested)
    private List<StudentDto> students;
    @Field
    private Integer studentSize;

    public EsDoctorInfo() {
    }

    public EsDoctorInfo(Long id, Faker faker, Random random) {

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
        this.availability = generateRandomChineseCharacters( RandomUtil.randomInt(2, 21));
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
        for (int i = 0; i < RandomUtil.randomInt(2,10); i++) {
            StudentDto studentDto = new StudentDto();
            studentDto.setId(RandomUtil.randomLong(20, 999999));
            studentDto.setName(faker.name().fullName());
            studentDto.setPhone(faker.phoneNumber().phoneNumber());
            objects.add(studentDto);
        }
        this.students = objects;
        this.studentSize = objects.size();
    }

    @Data
    public static class AddressInfo {
        @Field
        private String streetAddress;
        @Field
        private String city;
        @Field
        private String state;
        @Field
        private String postalCode;
        @Field
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
        @Field
        private String phoneNumber;
        @Field
        private String mobileNumber;
        @Field
        private String emergencyContactName;
        @Field
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
        @Field
        private String degree;
        @Field
        private String university;
        @Field
        private int graduationYear;
        @Field
        private String residencyProgram;
        @Field
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
        EsDoctorInfo doctor = new EsDoctorInfo();
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
        @Field
        private Long id;
        @Field
        private String name;
        @Field
        private String phone;
    }

    public static String generateRandomChineseCharacters(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int codePoint = RandomUtil.randomInt(MIN_UNICODE, MAX_UNICODE + 1);
            sb.append((char) codePoint);
        }
        return sb.toString();
    }
}
