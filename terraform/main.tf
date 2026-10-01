terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  backend "s3" {
    bucket = "maven-app-state-bucket"
    key    = "terraform.tfstate"
    region = "ap-southeast-2"
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_instance" "ec2_for_jenkins" {
  ami           = var.ami_id
  subnet_id     = aws_subnet.public.id
  instance_type = var.instance_type
  key_name      = var.key_name

  user_data = file("entry-script.sh")
  user_data_replace_on_change = true
  
  vpc_security_group_ids = [aws_security_group.maven_app_sg.id]

  tags = {
    Name = "ec2_for_jenkins"
  }
}