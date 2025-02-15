package com.emoldino.serenity.server.jpa.own.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.emoldino.serenity.server.env.Env
import com.emoldino.serenity.server.jpa.common.entity.BaseEntity
//import io.swagger.annotations.ApiModel
//import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate
import javax.persistence.*

@Entity(name = "Company")
//@ApiModel("가입한 회사")
@Table(name = Env.tablePrefix + "company")
@JsonInclude(JsonInclude.Include.NON_NULL)
open class Company(

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ag_id", columnDefinition = "char(36)", insertable = false, updatable = false)
  var agent: Agent? = null,

  /**
   * 가입회사 대행사 고유 ID
   */
//  @ApiModelProperty("가입회사 대행사 고유 ID")
  @Column(name = "ag_id", columnDefinition = "char(36)")
  var agId: String? = null,

  /**
   * 가입회사 등록 member 고유 ID
   */
//  @ApiModelProperty("가입회사 등록 member 고유 ID")
  @Column(name = "mb_id", columnDefinition = "char(36)")
  var mbId: String? = null,

  /**
   * 가입회사 코드
   */
//  @ApiModelProperty("가입회사 코드")
  @Column(name = "co_code", nullable = false)
  var coCode: String? = null,

  /**
   * 가입회사명
   */
//  @ApiModelProperty("가입회사명")
  @Column(name = "co_name", nullable = false)
  var coName: String? = null,

  /**
   * 0:개인, 1:법인, 2:협동조합...
   */
//  @ApiModelProperty("0:개인, 1:법인, 2:협동조합...")
  @Column(name = "co_type", nullable = false)
  var coType: Int = 0,

  /**
   * 가입회사 사업자번호
   */
//  @ApiModelProperty("가입회사 사업자번호")
  @Column(name = "co_reg_no", nullable = false)
  var coRegNo: String? = null,

  @Column(name = "co_contract_start")
  //@Contextual
  var coContractStart: LocalDate? = null,

  @Column(name = "co_contract_end")
  //@Contextual
  var coContractEnd: LocalDate? = null,

  /**
   * 상태 flag(-3: blocked, -2: withdrawal, -1: dormant, 0:wait, 1:company-otp-ceritified)
   */
  @Column(name = "co_status", nullable = false)
//  @ApiModelProperty("상태 flag(-3: blocked, -2: withdrawal, -1: dormant, 0:wait, 1:company-otp-ceritified)")
  var coStatus: Int = 0,

  /**
   * 상세정보 JSON
   */
  @Column(name = "co_prop")
//  @ApiModelProperty("상세정보 JSON")
  var coProp: String? = null,

  @Column(name = "co_phone")
  var coPhone: String? = null,

  @Column(name = "co_fax")
  var coFax: String? = null,

  @Column(name = "co_zip", columnDefinition = "char(8)")
  var coZip: String? = null,

  @Column(name = "co_addr1")
  var coAddr1: String? = null,

  @Column(name = "co_addr2")
  var coAddr2: String? = null,

  @Column(name = "co_latitude")
  var coLatitude: Double? = null,

  @Column(name = "co_longitude")
  var coLongitude: Double? = null,

  /**
   * 대행사 홈페이지
   */
  @Column(name = "co_homepage")
//  @ApiModelProperty("대행사 홈페이지")
  var coHomepage: String? = null,

//  @OneToMany(fetch = FetchType.LAZY, mappedBy = "company", cascade = [CascadeType.ALL])
//  var companyMemberList: MutableList<CompanyMember> = mutableListOf()

) : BaseEntity() {

  fun copy(agent: Agent) {
    this.agent = agent
  }

//  fun addCompanyMember(companyMember: CompanyMember) {
//    companyMember.copy(company = this)
//    companyMemberList.add(companyMember)
//  }

}
