package com.example.ddmdemo.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.Queries;
import java.util.List;
import java.util.Stack;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;


@Component
public class BooleanQueryParser {
    public List<String> parse(String query) {
        if (query == null || query.isBlank()) return Collections.emptyList();
        List<String> tokens = tokenize(query);
        return convertToPostfix(tokens);
    }

    private List<String> tokenize(String query) {
        List<String> tokens = new ArrayList<>();
        // Ovaj regex čuva fraze pod navodnicima kao jedan token
        Matcher m = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1|[^\\s()]+|[()]").matcher(query);
        while (m.find()) {
            tokens.add(m.group());
        }
        return tokens;
    }

    private List<String> convertToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        // Definisanje prioriteta: NOT je najjači, OR najslabiji
        Map<String, Integer> precedence = Map.of("NOT", 3, "AND", 2, "OR", 1);

        for (String token : tokens) {
            String upperToken = token.toUpperCase();
            if (precedence.containsKey(upperToken)) {
                while (!stack.isEmpty() && precedence.getOrDefault(stack.peek(), 0) >= precedence.get(upperToken)) {
                    output.add(stack.pop());
                }
                stack.push(upperToken);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop();
            } else {
                output.add(token); // Reči i "fraze pod navodnicima"
            }
        }
        while (!stack.isEmpty()) output.add(stack.pop());
        return output;
    }

    public Query buildComplexQuery(List<String> postfixTokens) {
        Stack<Query> stack = new Stack<>();

        for (String token : postfixTokens) {
            if (isOperator(token)) {
                if (token.equalsIgnoreCase("NOT")) {
                    Query q = stack.pop();
                    stack.push(Query.of(qBuilder -> qBuilder.bool(b -> b.mustNot(q))));
                } else {
                    Query right = stack.pop();
                    Query left = stack.pop();

                    if (token.equalsIgnoreCase("AND")) {
                        // Spajamo dva upita u Boolean MUST
                        stack.push(Query.of(qBuilder -> qBuilder.bool(b -> b.must(left, right))));
                    } else if (token.equalsIgnoreCase("OR")) {
                        // Spajamo dva upita u Boolean SHOULD
                        stack.push(Query.of(qBuilder -> qBuilder.bool(b -> b.should(left, right))));
                    }
                }
            } else {
                stack.push(createQueryForToken(token));
            }
        }
        return stack.pop();
    }

    private Query createQueryForToken(String token) {
        if (token.startsWith("\"") && token.endsWith("\"")) {
            String phrase = token.replace("\"", "");
            // MatchPhraseQuery za tačnu frazu
            return NativeQuery.builder()
                    .withQuery(q -> q.matchPhrase(m -> m.field("content").query(phrase)))
                    .build().getQuery();
        } else {
            // Običan MatchQuery za full-text
            return NativeQuery.builder()
                    .withQuery(q -> q.match(m -> m.field("content").query(token)))
                    .build().getQuery();
        }
    }

    public boolean isOperator(String token) {
        return List.of("AND", "OR", "NOT").contains(token.toUpperCase());
    }
}
