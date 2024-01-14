<h1>Intoduction</h1>
This tool is to initialize a Spring Boot and Java based multi module Microservices structure so that developer can start writing their logic without worrying about creating modules and configuring. This tool also has support for openAPI/Swagger codegen.

<h1>Prerequisites:</h1>
<table>
  <tbody>
    <tr>
      <td>Java 17</td>
    </tr>
    <tr>
      <td>Maven</td>
    </tr>
  </tbody>
</table>

If versions greater Java 17 is being used, change compiler version in pom.xml (https://github.com/vignesh-kg/ogier-ms-initializer/blob/master/pom.xml#L17) after cloning the project.

<h1>How to use the tool?</h1>

1. Clone the repository.
2. Build the Project using Maven (cmd: mvn clean install)
3. Add required run time arguments and run the application.

Now, the multi module Spring Boot application is ready for development.

<h1>Mandatory Arguments:</h1>
<table>
  <tr>
      <th>Argument</th>
      <th>Description</th>
      <th>Note</th>
    </tr>
  <tbody>
    <tr>
      <td>msName</td>
      <td>The Name of the Multimodule Project.</td>
      <td>msName will be used as artifact Id as well.</td>
    </tr>
    <tr>
      <td>directoryPath</td>
      <td>Path where the project is to be created</td>
      <td>Path should end with "/". Example: C:/path/to/project/ </td>
    </tr>
    <tr>
      <td>groupId</td>
      <td>Group Id of the project.</td>
      <td></td>
    </tr>
  </tbody>
</table>

<h1>Optional Arguments</h1>
<table>
  <tr>
      <th>Argument</th>
      <th>Description</th>
      <th>Note</th>
    </tr>
  <tbody>
    <tr>
      <td>apiYaml</td>
      <td>Path to API yaml</td>
      <td>Swagger or OpenAPI yaml documentation, which will be used to create Interface and Models</td>
    </tr>
  </tbody>
</table>

<h1>Modules that will be created</h1>
<table>
  <tr>
      <th>Module</th>
      <th>Description</th>
    </tr>
  <tbody>
    <tr>
      <td>Parent</td>
      <td>This Module is the Parent for other Modules. It manages the dependencies for it's child Modules</td>
    </tr>
    <tr>
      <td>api</td>
      <td>This Module contains only the POM to generate code for the services defined in swagger/openapi yaml</td>
    </tr>
    <tr>
      <td>async</td>
      <td>This Module contains only the POM to generate Models for Messaging Queues from Swagger/Openapi yaml</td>
    </tr>
     <tr>
      <td>resource</td>
      <td>This Module is where the Controller for the services generated from swagger/openAPI will be implemented.</td>
    </tr>
    <tr>
      <td>gateway</td>
      <td>This Module is to be used to implement code to call external Services or Consumer logic if Messaging Queue is used</td>
    </tr>
    <tr>
      <td>service</td>
      <td>This Module is to be used to handle Business logic</td>
    </tr>
    <tr>
      <td>persistence</td>
      <td>This Module is to be used to handle any DB/NoSQL/Data store transactions (CRUD)</td>
    </tr>
    <tr>
      <td>test</td>
      <td>This Module is to be used to have any Unit Tests</td>
    </tr>
  </tbody>
</table>
