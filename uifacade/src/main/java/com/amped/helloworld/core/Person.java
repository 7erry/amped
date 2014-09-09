package com.amped.helloworld.core;

import javax.persistence.*;

import com.wordnik.swagger.annotations.*;

@ApiModel(value = "A human being regarded as an individual")
@Entity
@Table(name = "people")
@NamedQueries({
    @NamedQuery(
        name = "com.amped.helloworld.core.Person.findAll",
        query = "SELECT p FROM Person p"
    )
})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "jobTitle", nullable = false)
    private String jobTitle;

    public long getId() {
        return id;
    }

    @ApiModelProperty(value = "Person ID", required=true)
    public void setId(long id) {
        this.id = id;
    }

    @ApiModelProperty(value = "Person Full Name", required=true)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    @ApiModelProperty(value = "Person Job Title", required=true, allowableValues = "Worker, Manager, Destroyer")
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
