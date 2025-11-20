## Tech stack 
- Backend: Spring Boot, Java 17, JPA/Hibernate, Maven
- Frontend: Next.js, React, TypeScript, Tailwind CSS
- Database: H2
- External Services: LB API for currency rates (https://www.lb.lt/webservices/FxRates/FxRates.asmx)

## To run backend
in `exchange-rate-portal` run:

mvn spring-boot:run

Backend will run on: http://localhost:8080

## To run frontend 
Navigate to the `client` folder:

cd client

Install Dependencies:

npm install

Run the Development Server:

npm run dev

Frontend will run on: http://localhost:3000