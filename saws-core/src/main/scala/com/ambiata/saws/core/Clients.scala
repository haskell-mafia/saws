package com.ambiata.saws
package core

import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.client.builder.AwsSyncClientBuilder
import com.amazonaws.services.cloudwatch._
import com.amazonaws.services.ec2._
import com.amazonaws.services.elasticmapreduce._
import com.amazonaws.services.identitymanagement._
import com.amazonaws.services.s3._
import com.amazonaws.services.simpleemail._

object Clients {

  def s3 = configured(AmazonS3ClientBuilder.standard(), "s3-ap-southeast-2.amazonaws.com", "ap-southeast-2")

  def ec2 = configured(AmazonEC2ClientBuilder.standard(), "ec2.ap-southeast-2.amazonaws.com", "ap-southeast-2")

  def iam = configured(AmazonIdentityManagementClientBuilder.standard(), "https://iam.amazonaws.com", "us-east-1")

  def emr = configured(AmazonElasticMapReduceClientBuilder.standard(), "elasticmapreduce.ap-southeast-2.amazonaws.com", "ap-southeast-2")

  def ses = configured(AmazonSimpleEmailServiceClientBuilder.standard(), "email.us-east-1.amazonaws.com", "us-east-1")

  def cw = configured(AmazonCloudWatchClientBuilder.standard(), "monitoring.ap-southeast-2.amazonaws.com", "ap-southeast-2")

  def configured[A <: AmazonWebServiceClient](a: A, endpoint: String): A = {
    a.setEndpoint(endpoint)
    a
  }

  def configured[Builder <: AwsSyncClientBuilder[Builder, Client], Client](builder: AwsSyncClientBuilder[Builder, Client], endpoint: String, region: String): Client = {
    builder.withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
      .build()
  }
}
