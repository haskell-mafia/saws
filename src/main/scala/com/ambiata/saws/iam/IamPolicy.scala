package com.ambiata.saws
package iam


case class IamPolicy(policyName: String, policyDocument: String)

object IamPolicy {

  def S3ReadPathPolicy(path: String): IamPolicy = {
    val name = s"ReadAccessTo_$path".replace('/', '+')
    IamPolicy(name, S3AccessPathPolicyWith(path, Seq("GetObject")))
  }

  def S3WritePathPolicy(path: String): IamPolicy  = {
    val name = s"WriteAccessTo_$path".replace('/', '+')
    IamPolicy(name, S3AccessPathPolicyWith(path, Seq("PutObject")))
  }

  def S3ReadWritePathPolicy(path: String): IamPolicy  = {
    val name = s"ReadWriteAccessTo_$path".replace('/', '+')
    IamPolicy(name, S3AccessPathPolicyWith(path, Seq("PutObject", "GetObject")))
  }

  def S3AccessPathPolicyWith(path: String, actions: Seq[String]) = {
    val s3Actions = actions.map(a => s""""s3:${a}"""").mkString(",")
    s"""|{
        |  "Version": "2012-10-17",
        |  "Statement": [
        |    {
        |      "Action": [ ${s3Actions} ],
        |      "Resource": [ "arn:aws:s3:::$path/*" ],
        |      "Effect": "Allow"
        |    }
        |  ]
        |}""".stripMargin
  }
}