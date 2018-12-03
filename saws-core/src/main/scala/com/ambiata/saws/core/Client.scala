package com.ambiata.saws
package core

import com.amazonaws.services.cloudwatch.AmazonCloudWatch
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce

case class Client[A](get: () => A)

object Client {
  def apply[A: Client] = implicitly[Client[A]]

  implicit def AmazonEC2ClientInstance: Client[AmazonEC2] = Client(() => Clients.ec2)
  implicit def AmazonS3ClientInstance: Client[AmazonS3] = Client(() => Clients.s3)
  implicit def AmazonCloudWatchClientInstance: Client[AmazonCloudWatch] = Client(() => Clients.cw)
  implicit def AmazonIdentityManagementClientInstance: Client[AmazonIdentityManagement] = Client(() => Clients.iam)
  implicit def AmazonElasticMapReduceClientInstance: Client[AmazonElasticMapReduce] = Client(() => Clients.emr)
  implicit def Tuple2Client[A: Client, B: Client]: Client[(A, B)] = Client(() => (Client[A].get(), Client[B].get()))
  implicit def Tuple3Client[A: Client, B: Client, C: Client]: Client[(A, B, C)] = Client(() => (Client[A].get(), Client[B].get(), Client[C].get()))
  implicit def Tuple4Client[A: Client, B: Client, C: Client, D: Client]: Client[(A, B, C, D)] = Client(() => (Client[A].get(), Client[B].get(), Client[C].get(), Client[D].get()))
  implicit def Tuple5Client[A: Client, B: Client, C: Client, D: Client, E: Client]: Client[(A, B, C, D, E)] = Client(() => (Client[A].get(), Client[B].get(), Client[C].get(), Client[D].get(), Client[E].get()))
}
