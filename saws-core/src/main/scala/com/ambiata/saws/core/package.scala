package com.ambiata.saws

import com.amazonaws.services.cloudwatch.AmazonCloudWatch
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce

import scalaz._, Scalaz._
import scalaz.effect._
import com.ambiata.mundane.control._

package object core {
  type Log = Vector[AwsLog]
  type AwsActionResult[A] = (Vector[AwsLog], Result[A])

  type S3Action[A] = Aws[AmazonS3, A]
  type EC2Action[A] = Aws[AmazonEC2, A]
  type IAMAction[A] = Aws[AmazonIdentityManagement, A]
  type EMRAction[A] = Aws[AmazonElasticMapReduce, A]
  type CloudWatchAction[A] = Aws[AmazonCloudWatch, A]
  type S3EC2Action[A] = Aws[(AmazonS3, AmazonEC2), A]
  type EC2IAMAction[A] = Aws[(AmazonEC2, AmazonIdentityManagement), A]
  type S3EC2IAMAction[A] = Aws[(AmazonS3, AmazonEC2, AmazonIdentityManagement), A]
  type IAMEMRAction[A] = Aws[(AmazonIdentityManagement, AmazonElasticMapReduce), A]

  type S3EC2 = (AmazonS3, AmazonEC2)
  type EC2IAM = (AmazonEC2, AmazonIdentityManagement)
  type S3EC2IAM = (AmazonS3, AmazonEC2, AmazonIdentityManagement)
  type IAMEMR = (AmazonIdentityManagement, AmazonElasticMapReduce)

}
