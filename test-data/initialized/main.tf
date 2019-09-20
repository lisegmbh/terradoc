resource "null_resource" "test_resource_1" {

}

resource "null_resource" "test_resource_2" {
  depends_on = [null_resource.test_resource_1]
}