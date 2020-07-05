import numpy as np

lista = []


def appi():
    global lista
    lista += [1, 2]
    lista += [3, 21, 1]


appi()

print(str(lista).strip('[]'))
