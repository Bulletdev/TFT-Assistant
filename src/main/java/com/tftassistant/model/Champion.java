package com.tftassistant.model;

import java.util.List;
import java.util.Objects; // Importar para equals/hashCode
import java.util.Set;

/**
 * Representa um campeão dentro do contexto da aplicação.
 * Após a refatoração para usar dados dinâmicos, esta classe armazena principalmente
 * o nome do campeão, que é usado como chave para buscar dados detalhados
 * no CompositionAnalyzer.
 */
public class Champion {
    private String name;
    // Campos como tier, synergies, primaryRole foram removidos pois agora
    // são buscados dinamicamente no CompositionAnalyzer usando o nome.
    // Outros campos (vida, itens, posição) podem ser adicionados aqui se necessário
    // para representar o estado do campeão no jogo ou na UI.

    // Construtor
    public Champion(String name) {
        this.name = Objects.requireNonNull(name, "Nome do campeão não pode ser nulo");
    }

    // Getter
    public String getName() {
        return name;
    }

    // toString simplificado
    @Override
    public String toString() {
        return "Champion{" +
               "name='" + name + '\'' +
               '}';
    }

    // TODO: Implementar equals() e hashCode() corretamente baseado no nome,
    //       se for usar Champions em Sets ou como chaves de Map.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Champion champion = (Champion) o;
        return Objects.equals(name, champion.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}