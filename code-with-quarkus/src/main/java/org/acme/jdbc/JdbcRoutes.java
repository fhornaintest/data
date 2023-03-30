/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.jdbc;

import org.apache.camel.builder.RouteBuilder;

public class JdbcRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("timer://insertCamel1?repeatCount=1")
                .log("INITIALIZATION RECORD TABLE")
                .setBody().simple("INSERT INTO record (old_record_number,new_record_number) VALUES (0,0)")
		.to("jdbc:camel-ds");

        from("timer://insertCamel2?period=1000")
                .log("## COUNT THE NUMBER OF RECORDS PRESENT IN THE SENSOR TABLE ##")
                .log("SELECT RECORD NUMBER FROM SENSOR TABLE")
		.setBody(simple("SELECT COUNT(*) AS NUMBER FROM sensors;"))
		.to("jdbc:camel-ds")
		.log("WE HAVE ${body} RECORDS IN THE SENSORS TABLE.")
		.split(body())
			.to("bean:RecordProcessor")
			.to("direct:PathA");

        from("direct:PathA")
                .log("##CHECK IF THERE IS  A NEW TIMESTAMP FROM SENSOR TABLE ##")
		.log("WE HAVE ${body[NUMBER]} RECORDS IN THE SENSORS TABLE.")
		.setBody().simple("UPDATE record SET new_record_number = '${body[NUMBER]}' LIMIT 1;;")
		.to("jdbc:camel-ds")
		.setBody().simple("SELECT old_record_number, new_record_number FROM record;")
		.to("jdbc:camel-ds")
		.split(body())
			.to("bean:RecordProcessor")
			.log("OLD RECORD NUMBER ${body[old_record_number]}")
			.log("NEW RECORD NUMBER ${body[new_record_number]}")
		.choice()
			.when(simple("${body[old_record_number]} == ${body[new_record_number]}"))
				.log("++++++++++++++++> WE DO NOTHING !")
			.when(simple("${body[old_record_number]} < ${body[new_record_number]}"))
				.setBody().simple("UPDATE record SET old_record_number = '${body[new_record_number]}' LIMIT 1;;")
				.to("jdbc:camel-ds")
            			.to("direct:PathB")
        		.otherwise()
            			.log(" !!! THERE IS AN ERROR HERE !!! ");

	from("direct:PathB")
		.setBody(simple("SELECT id, timestamp FROM sensors ORDER BY ID DESC LIMIT 1;"))
		.to("jdbc:camel-ds")
                .split(body())
                        .to("bean:RecordProcessor")		
			.setBody(simple("INSERT INTO monitoring (timestamp) VALUE ('${body[timestamp]}');"))
			.to("jdbc:camel-ds");
		 
    }
}
