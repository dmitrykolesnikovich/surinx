/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.headius.surinx;

import java.dyn.CallSite;
import java.dyn.Linkage;
import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author headius
 */
public class SimpleJavaBootstrap {
    public static CallSite bootstrap(Class caller, String name, MethodType type) {
        CallSite site = new CallSite(caller, name, type);
        site.setTarget(MethodHandles.collectArguments(MethodHandles.insertArguments(FALLBACK, 0, site), type));
        return site;
    }

    public static void registerBootstrap(Class cls) {
        Linkage.registerBootstrapMethod(cls, BOOTSTRAP);
    }

    public static final MethodHandle BOOTSTRAP = MethodHandles.lookup().findStatic(SimpleJavaBootstrap.class, "bootstrap", Linkage.BOOTSTRAP_METHOD_TYPE);

    public static Object fallback(CallSite site, Object receiver, Object[] args) throws Throwable {
        Method rMethod;
        MethodHandle target = null;
        if (site.name().equals("+")) {
            // primitive math
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod("plus", argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("-")) {
            // primitive math
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod("minus", argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("/")) {
            // primitive math
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod("div", argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("*")) {
            // primitive math
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod("mul", argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("==")) {
            // booleans return non-null on truth
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
		    try {
            	rMethod = SimpleJavaBootstrap.class.getMethod("equals", argTypes);
            } catch (NoSuchMethodException nsme) {
				// use default .equals(Object)
				rMethod = Object.class.getMethod("equals", Object.class);
			}
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("__lt__")) {
            // booleans return non-null on truth
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod(site.name(), argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("__gt__")) {
            // booleans return non-null on truth
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod(site.name(), argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("__le__")) {
            // booleans return non-null on truth
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod(site.name(), argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else if (site.name().equals("__ge__")) {
            // booleans return non-null on truth
            Class[] argTypes = new Class[args.length + 1];
            argTypes[0] = receiver.getClass();
            for (int i = 0; i < args.length; i++) {
                argTypes[i + 1] = args[i].getClass();
            }
            rMethod = SimpleJavaBootstrap.class.getMethod(site.name(), argTypes);
            target = MethodHandles.lookup().unreflect(rMethod);
            target = MethodHandles.convertArguments(target, site.type());
        } else {
            // look for exact match for arg types
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < argTypes.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            
            Class rClass = null;
            if (receiver == null || receiver.getClass() == Class.class) {
				if (receiver == null) {
                	rClass = site.callerClass();
                } else {
	      			rClass = (Class)receiver;
                }

				if (site.name().equals("new")) {
					// constructor calls not working in MLVM right now
	                // try {
	                //     Constructor rConstructor = rClass.getConstructor(argTypes);
	                //     target = MethodHandles.lookup().unreflectConstructor(rConstructor);
	                // } catch (NoSuchMethodException nsme) {
	                //     // hacky...try with all Object
	                //     for (int i = 0; i < argTypes.length; i++) {
	                //         argTypes[i] = Object.class;
	                //     }
	                //     Constructor rConstructor = rClass.getConstructor(argTypes);
	                //     target = MethodHandles.lookup().unreflectConstructor(rConstructor);
	                // }
					
		            argTypes = new Class[args.length + 1];
		            argTypes[0] = Class.class;
		            for (int i = 0; i < args.length; i++) {
		                argTypes[i + 1] = Object.class;
		            }
		            rMethod = SimpleJavaBootstrap.class.getMethod("__new__", argTypes);
		            target = MethodHandles.lookup().unreflect(rMethod);
		            target = MethodHandles.convertArguments(target, site.type());
		
					site.setTarget(target);
					return __new__(rClass, args);
				} else {
	                try {
	                    rMethod = rClass.getMethod(site.name(), argTypes);
	                    target = MethodHandles.lookup().unreflect(rMethod);
	                } catch (NoSuchMethodException nsme) {
	                    // hacky...try with all Object
	                    for (int i = 0; i < argTypes.length; i++) {
	                        argTypes[i] = Object.class;
	                    }
	                    rMethod = rClass.getMethod(site.name(), argTypes);
	                    target = MethodHandles.lookup().unreflect(rMethod);
	                }	
	                target = MethodHandles.dropArguments(target, 0, Object.class);
                    target = MethodHandles.convertArguments(target, site.type());
				}
            } else {
                rClass = receiver.getClass();
                rMethod = rClass.getMethod(site.name(), argTypes);
                target = MethodHandles.lookup().unreflect(rMethod);
                target = MethodHandles.convertArguments(target, site.type());
            }
        }

        site.setTarget(target);
        
        Object result = null;
        switch (args.length) {
        case 0:
            result = MethodHandles.invoke(target, receiver);
            break;
        case 1:
            result = MethodHandles.invoke(target, receiver, args[0]);
            break;
        case 2:
            result = MethodHandles.invoke(target, receiver, args[0], args[1]);
            break;
        case 3:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2]);
            break;
        case 4:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2], args[3]);
            break;
        case 5:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2], args[3], args[4]);
            break;
        case 6:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2], args[3], args[4], args[5]);
            break;
        case 7:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            break;
        case 8:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            break;
        case 9:
            result = MethodHandles.invoke(target, receiver, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            break;
        default:
            throw new RuntimeException("unsupported arity: " + args.length);
        }
        return result;
    }

    public static final MethodHandle FALLBACK = MethodHandles.lookup().findStatic(SimpleJavaBootstrap.class, "fallback", MethodType.make(Object.class, CallSite.class, Object.class, Object[].class));

    public static final Boolean __lt__(Long a, Long b) {
        return a < b;
    }

    public static final Boolean __gt__(Long a, Long b) {
        return a > b;
    }

	public static final Boolean __gt__(Double a, Long b) {
		return a > b;
	}

    public static final Boolean __le__(Long a, Long b) {
        return a <= b;
    }

    public static final Boolean __ge__(Long a, Long b) {
        return a >= b;
    }

    public static final Long plus(Long a, Long b) {
        return a + b;
    }

    public static final Double plus(Double a, Double b) {
        return a + b;
    }

    public static final Double plus(Double a, Long b) {
        return a + b;
    }

    public static final Double plus(Long a, Double b) {
        return a + b;
    }

	public static final String plus(String a, Double b) {
		return a + b;
	}

	public static final String plus(String a, Long b) {
		return a + b;
	}

	public static final String plus(String a, String b) {
		return a + b;
	}

    public static final Long minus(Long a, Long b) {
        return a - b;
    }

    public static final Double minus(Double a, Double b) {
        return a - b;
    }

    public static final Double minus(Double a, Long b) {
        return a - b;
    }

    public static final Double minus(Long a, Double b) {
        return a - b;
    }

	public static final Double div(Long a, Double b) {
		return a / b;
	}
	
	public static final Double mul(Double a, Double b) {
		return a * b;
	}

    public static final Boolean equals(Long a, Long b) {
        return a.equals(b);
    }

    public static final Boolean equals(Double a, Double b) {
        return a.equals(b);
    }

    public static final Boolean equals(Double a, Long b) {
        return ((Double)(double)b).equals(a);
    }

    public static final Boolean equals(Long a, Double b) {
        return ((Double)(double)a).equals(b);
    }

    public static final Boolean equals(Integer a, Integer b) {
        return a.equals(b);
    }

    public static final Boolean equals(Integer a, Long b) {
        // have to upcast or it always returns false
        return ((Long)(long)a).equals(b);
    }

    public static final Boolean equals(Long a, Integer b) {
        // have to upcast or it always returns false
        return ((Long)(long)b).equals(a);
    }

    public static final Boolean equals(Integer a, Double b) {
        // have to upcast or it always returns false
        return ((Double)(double)a).equals(b);
    }

    public static final Boolean equals(Double a, Integer b) {
        // have to upcast or it always returns false
        return ((Double)(double)b).equals(a);
    }

    public static final Boolean equals(Boolean a, Boolean b) {
        return a == b;
    }

	public static final Object __new__(Class target) {
		try {
			return target.newInstance();
		} catch (InstantiationException ie) {
			throw new RuntimeException(ie);
		} catch (IllegalAccessException iae) {
			throw new RuntimeException(iae);
		}
	}

	public static final Object __new__(Class target, Object arg0) {
		Constructor c;
		try {
			c = target.getConstructor(arg0.getClass());
			return c.newInstance(arg0);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException(nsme);
		} catch (InstantiationException ie) {
			throw new RuntimeException(ie);
		} catch (IllegalAccessException iae) {
			throw new RuntimeException(iae);
		} catch (InvocationTargetException ite) {
			throw new RuntimeException(ite);
		}
	}

	public static final Object __new__(Class target, Object arg0, Object arg1) {
		Constructor c;
		try {
			c = target.getConstructor(arg0.getClass(), arg1.getClass());
			return c.newInstance(arg0, arg1);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException(nsme);
		} catch (InstantiationException ie) {
			throw new RuntimeException(ie);
		} catch (IllegalAccessException iae) {
			throw new RuntimeException(iae);
		} catch (InvocationTargetException ite) {
			throw new RuntimeException(ite);
		}
	}

	public static final Object __new__(Class target, Object arg0, Object arg1, Object arg2) {
		Constructor c;
		try {
			c = target.getConstructor(arg0.getClass(), arg1.getClass(), arg2.getClass());
			return c.newInstance(arg0, arg1, arg2);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException(nsme);
		} catch (InstantiationException ie) {
			throw new RuntimeException(ie);
		} catch (IllegalAccessException iae) {
			throw new RuntimeException(iae);
		} catch (InvocationTargetException ite) {
			throw new RuntimeException(ite);
		}
	}

	public static final Object __new__(Class target, Object[] args) {
		Class[] argTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) argTypes[i] = args[i].getClass();
		Constructor c;
		try {
			c = target.getConstructor(argTypes);
			return c.newInstance(args);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException(nsme);
		} catch (InstantiationException ie) {
			throw new RuntimeException(ie);
		} catch (IllegalAccessException iae) {
			throw new RuntimeException(iae);
		} catch (InvocationTargetException ite) {
			throw new RuntimeException(ite);
		}
	}
}
