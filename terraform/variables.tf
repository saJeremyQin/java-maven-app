variable "vpc_cidr" {
  description = "The CIDR block for the VPC"
  default     = "10.0.0.0/16"
  type        = string
}

variable "environment" {
  description = "The environment for the resources (e.g., dev, prod)"
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "ap-southeast-2"
}

variable "ami_id" {
  description = "The AMI ID for the EC2 instance"
  type        = string
  default     = "ami-0720cb7af233b0529"
}

variable "instance_type" {
  description = "The instance type for the EC2 instance"
  type        = string
  default     = "t2.micro"
} 

variable "jenkins_ip" {
  description = "Public CIDR allowed to SSH to the instance, e.g. 203.0.113.10/32"
  type        = string
  default     = "134.199.156.14/32"
}

variable "key_name" {
  description = "The name of the SSH key pair to use for the EC2 instance"
  type        = string
  default     = "maven-app-key"
}

variable "my_ip" {
  description = "The public CIDR of the user's IP allowed to SSH to the instance, e.g. 203.0.113.10/32"
  type        = string
}