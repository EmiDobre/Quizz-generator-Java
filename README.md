DOBRE EMILIA ILIANA - 323 CB 


---
FLUX PROGRAM:
- 
Aplicatia contine useri, intrebari, chestionare, actiuni astfel am creat o clasa ParserBase care contine entitatile folosite in aplicate ce se vor initializa dupa parsarea mainului.

Pentru nevoia de manipulare a fisierelor aplicatia contine si un atribut comanda prin care se pot accesa
operatiile.

Din main se parseaza prima data argumentele, se initializeaza atributele clasei de baza, se verifica erorile,
apoi se executa metoda ceruta de actiune.
Inainte de orice metoda se verifica autentificarea/logarea si erorile specifice ce pot aparea.

---
1)Functii fisiere
-

-append_in file scrie intr-un fisier un String array pe care il scrie linie cu linie 

-find_infile: citeste dintr-un fisier linie cu linie si intoarce linia pe care s-a gasit stringul de cautat.
Formatul fiind csv pentru toate fisierele, stringul cautat se afla la o pozitie relativa de virgula pe acea linie.

-readAll face din fisierul primit un String Array din liniile lui si evita daca este cazul anumite linii



---
2)Create User/Question/Quizz  
-
-se creeaza un fisier users.csv unde o linie are: userName,Parola 

-se creeazaun fisier questions.csv unde informatia despre o intrebare se retine pe mai multe linii

- prima linie: text intrebare,nr raspunsuri,nr raspunsuri corecte, id
- urmeaza apoi un nr raspunsuri linii cu: -answer-id 'text', val de adevar: correct sau no
- id ul se completeaza pe linia de inceput ce descrie o intrebare, in momentul in care se scrie
- functia de generare id calculeaza numarul de linii dintr un fisier care are doar liniile descriptive ale intrebarilor 
- pentru aflarea id ului din fisier al raspunsurilor, parcurg raspunsurile actuale apelez findrealId care imita logica de la generate id


-se creeaza un fisier quizz.csv acc idee: 

- prima linie: 'userName' care a creat quizzul, nume quizz, nr intrebari, id quizz
- urmatoarele nr intrebari linii cu: -question-.. 'id-ul intrebarii ce se va gasi in questions.csv'


---
3)Get Question ID by name/Get all questions
-
- caut in fisier dupa id intorc linia si aflu apoi id ul care se afla pe linie
- pentru a afisa toate intrebarile fac un vector de stringuri din toate liniile fisierului fara cele care contin date despre raspunsuri

---
4)Get Quizz ID by name/Get all quizz/Get quizz details
-

-apare nevoia de verificare daca quizzul a fost sau nu completat astfel de fiecare data cand se da submit se creeaza
un fisier submit.csv care contine pe fiecare linie informatii despre cate un singur quizz submis

- o linie in submit: 'userul care l-a completat', nume quizz, nr intrebari, id quizz, punctaj obtinut

-pentru a afisa detaliile, pentru fiecare quizz aflu numarul de intrebari, le gasesc dupa id in fisierul de intrebari
-pentru fiecare intrebare ii gasesc linia in fisier, pentru ca mai apoi sa pot accesa urmatoarele linii din fisier ce reprezinta raspunsurile
-pentru fiecare raspuns afisez detaliile sale 


---
5)Submit Quizz/Get solutions
-

-pentru fiecare raspuns primit pentru submis verific printr-un boolean daca a fost gasit id ul
sau la o intrebare ce apartine quizzului la care se da submit

-incep sa verific cautand in toate intrebarile pe care le are quizzul, aflu linia unde se afla intrebarea curenta
in fisierul de intrebari 
-parcurg raspunsurile pana cand se gaseste id-ul raspunsului dat pentru submitere
-se face scorul prin functia de submit care primeste ca parametru corectitudinea raspunsului submis, intrebarea auxiliara
creata si scorul anterior
-functia de submit calculeaza ponderea pentru acea intrebare

-la final, cand au fost verificate toate intrebarile submise si scorul s-a adunat, in functie de numarul de intrebari din
quiz aflu punctajul final
-se adauga quizzul cu userul care l-a completat, numele,nr intrebari,id si punctaj in fisierul de submit

-pentru getSolutions se parcurge fisierul submit si se afiseaza liniile care contin userul

---
6)Delete quizz
-

-fisierul de quizz il parcurg si retin prima jumatate a lui intr-un String Array
-prima jumatate pana ce da de prima linie ce contine quizzul ce trebuie sters
-sar peste nr de intrebari ale quizzului si adaug restul informatiie ramase din fisier
-sterg fisierul si il scriu cu noua informatie


---
7)Bonus
-
-Cazuri limita:

- getSolutions: nu exista niciun quizz submis
- getSolutions: userul nu a submis inca nimic
- createQuestion: nu este mentionat tipul 

