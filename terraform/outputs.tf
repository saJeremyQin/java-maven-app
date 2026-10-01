output "public_ec2_ip" {
  value = aws_instance.ec2_for_jenkins.public_ip
}