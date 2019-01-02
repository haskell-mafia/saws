package com.ambiata.saws
package core

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.client.builder.AwsSyncClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudwatch._
import com.amazonaws.services.ec2._
import com.amazonaws.services.elasticmapreduce._
import com.amazonaws.services.identitymanagement._
import com.amazonaws.services.s3._
import com.amazonaws.services.simpleemail._

object Clients {

  def s3 = configured(AmazonS3ClientBuilder.standard(), Regions.AP_SOUTHEAST_2)

  def ec2 = configured(AmazonEC2ClientBuilder.standard(), Regions.AP_SOUTHEAST_2)

  def iam = configured(AmazonIdentityManagementClientBuilder.standard(), Regions.AP_SOUTHEAST_2)

  def emr = configured(AmazonElasticMapReduceClientBuilder.standard(), Regions.AP_SOUTHEAST_2)

  // only valid regions are us-east-1, us-west-2, eu-west-1
  def ses = configured(AmazonSimpleEmailServiceClientBuilder.standard(), Regions.US_EAST_1)

  def cw = configured(AmazonCloudWatchClientBuilder.standard(), Regions.AP_SOUTHEAST_2)

  def configured[Builder <: AwsSyncClientBuilder[Builder, AwsClient], AwsClient](builder: AwsSyncClientBuilder[Builder, AwsClient], region: Regions): AwsClient = {
    builder.withRegion(region).build()
  }

  /**
    * This should only be used when a non-standard endpoint is being used, AWS is much better at working out what the endpoint should be.
    */
  def configured[Builder <: AwsSyncClientBuilder[Builder, AwsClient], AwsClient](builder: AwsSyncClientBuilder[Builder, AwsClient], endpoint: String, region: String): AwsClient = {
    builder.withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
      .build()
  }
}
