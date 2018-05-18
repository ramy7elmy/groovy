/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package groovy.bugs

import gls.CompilableTestSupport

class Groovy6171Bug extends CompilableTestSupport {
    void testGroovy6171() {
        shouldCompile '''
        public class Foo<T extends List<X>, X extends Number> {}
        '''
    }

    void testIncompatibleType1() {
        def errMsg = shouldFail '''
        @groovy.transform.CompileStatic
        public class Foo<T extends List<X>, X extends Number> {
            static void main(String[] args) {
                def f = new Foo<ArrayList<String>, String>()
            }
        }
        '''

        assert errMsg.contains('The type String is not a valid substitute for the bounded parameter <X extends java.lang.Number>')
    }

    void testIncompatibleType2() {
        def errMsg = shouldFail '''
        @groovy.transform.CompileStatic
        public class Foo<T extends List<X>, X extends Number> {
            static void main(String[] args) {
                def f = new Foo<HashSet<Integer>, Integer>()
            }
        }
        '''

        assert errMsg.contains('The type HashSet is not a valid substitute for the bounded parameter <T extends java.util.List<X>>')
    }

    void test2() {
        assertScript '''
        @groovy.transform.CompileStatic
        public class Foo<T extends List<X>, X extends Number> {
            X getFirstElement(T t) {
                X x = t.get(0)
                return x
            }
            
            static void main(String[] args) {
                def f = new Foo<ArrayList<Integer>, Integer>()
                def list = new ArrayList<Integer>()
                list.add(123)
                assert 123 == f.getFirstElement(list)
            }
        }
        '''
    }

    void test3() {
        assertScript '''
        @groovy.transform.CompileStatic
        public class Foo<T extends List<X>, X extends Number> {
            X getFirstElement(List<X> list) {
                X x = list.get(0)
                
                assert Number == x.getClass().getGenericSuperclass()
                
                return x
            }
            
            Number getFirstNumber(T t) {
                return getFirstElement(t)
            }
            
            static void main(String[] args) {
                def f = new Foo<ArrayList<Integer>, Integer>()
                def list = new ArrayList<Integer>()
                list.add(123)
                assert 123 == f.getFirstNumber(list)
            }
        }
        '''
    }
}