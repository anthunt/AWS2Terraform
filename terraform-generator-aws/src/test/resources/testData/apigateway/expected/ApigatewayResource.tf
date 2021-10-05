resource aws_api_gateway_resource PetStore-mvv3y4 {
	rest_api_id  = aws_api_gateway_rest_api.PetStore.id
	parent_id = aws_api_gateway_resource.PetStore-px3r5v.id
	path_part  = "{petId}"
}

resource aws_api_gateway_method PetStore-mvv3y4-GET {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-mvv3y4.id
	http_method = "GET"
	authorization = "NONE"
	authorizer_id = null
	request_parameters = {
		"method.request.path.petId" = true
	}
}

resource aws_api_gateway_integration PetStore-mvv3y4-GET {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-mvv3y4.id
	http_method = "GET"
	type = "HTTP"
	connection_type = "INTERNET"
	connection_id = null
	uri = "http://petstore.execute-api.ap-northeast-2.amazonaws.com/petstore/pets/{petId}"
	integration_http_method = "GET"
	request_parameters = {
		"integration.request.path.petId" = "method.request.path.petId"
	}
}

resource aws_api_gateway_method PetStore-mvv3y4-OPTIONS {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-mvv3y4.id
	http_method = "OPTIONS"
	authorization = "NONE"
	authorizer_id = null
	request_parameters = {
		"method.request.path.petId" = true
	}
}

resource aws_api_gateway_integration PetStore-mvv3y4-OPTIONS {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-mvv3y4.id
	http_method = "OPTIONS"
	type = "MOCK"
	connection_type = null
	connection_id = null
	uri = null
	integration_http_method = null
	request_parameters = {
	}
}

resource aws_api_gateway_resource PetStore-px3r5v {
	rest_api_id  = aws_api_gateway_rest_api.PetStore.id
	parent_id = aws_api_gateway_rest_api.PetStore.root_resource_id
	path_part  = "pets"
}

resource aws_api_gateway_method PetStore-px3r5v-POST {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-px3r5v.id
	http_method = "POST"
	authorization = "NONE"
	authorizer_id = null
	request_parameters = {
	}
}

resource aws_api_gateway_integration PetStore-px3r5v-POST {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-px3r5v.id
	http_method = "POST"
	type = "HTTP"
	connection_type = "INTERNET"
	connection_id = null
	uri = "http://petstore.execute-api.ap-northeast-2.amazonaws.com/petstore/pets"
	integration_http_method = "POST"
	request_parameters = {
	}
}

resource aws_api_gateway_method PetStore-px3r5v-GET {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-px3r5v.id
	http_method = "GET"
	authorization = "NONE"
	authorizer_id = null
	request_parameters = {
		"method.request.querystring.type" = false
		"method.request.querystring.page" = false
	}
}

resource aws_api_gateway_integration PetStore-px3r5v-GET {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-px3r5v.id
	http_method = "GET"
	type = "HTTP"
	connection_type = "INTERNET"
	connection_id = null
	uri = "http://petstore.execute-api.ap-northeast-2.amazonaws.com/petstore/pets"
	integration_http_method = "GET"
	request_parameters = {
		"integration.request.querystring.page" = "method.request.querystring.page"
		"integration.request.querystring.type" = "method.request.querystring.type"
	}
}

resource aws_api_gateway_method PetStore-px3r5v-OPTIONS {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-px3r5v.id
	http_method = "OPTIONS"
	authorization = "NONE"
	authorizer_id = null
	request_parameters = {
	}
}

resource aws_api_gateway_integration PetStore-px3r5v-OPTIONS {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	resource_id = aws_api_gateway_resource.PetStore-px3r5v.id
	http_method = "OPTIONS"
	type = "MOCK"
	connection_type = null
	connection_id = null
	uri = null
	integration_http_method = null
	request_parameters = {
	}
}

