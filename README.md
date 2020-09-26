# KDC
Desenvolva um programa que implemente o centro de distribuição de chaves (KDC). O programa é composto de duas entidades (Alice e Bob) que desejam conversar utilizando criptografia simétrica. Os seguintes requisitos devem ser atendidos:

1. Bob e o KDC devem compartilhar uma chave mestre;
2. Ana e o KDC devem compartilhar uma chave mestre;
3. Bob e Ana devem conversar através de uma chave de sessão;
4. A chave de sessão deve ser obtida através de uma comunicação criptografada com o KDC, utilizando a chave mestre;
5. Quando ambas entidades possuírem a chave de sessão, Ana gera um nonce e encaminha para Bob, cifrando;
6. Bob responde Ana executando uma função sobre o nonce recebido, cifrando;
7. Ana compara o valor recebido com o valor de nonce enviado realizando a função;
