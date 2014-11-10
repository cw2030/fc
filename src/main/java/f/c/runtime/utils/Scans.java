package f.c.runtime.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import f.c.common.annotation.Inject;
import f.c.common.annotation.IocBean;
import f.c.common.services.ILoggerService;
import f.c.common.services.LoggerService;

class Scans {

    private ILoggerService LOG = LoggerService.getLog(Scans.class);
    public static Scans INSTANCE = new Scans();
    private static final String FLT_CLASS = "^.+[.]class$";

    private Scans() {}

    public Set<Class<?>> scanPackage(String pkg) {
        if (pkg == null || pkg.trim().equals(""))
            return new LinkedHashSet<Class<?>>();
        return loadClass(pkg, FLT_CLASS);
    }

    public List<Properties> scanProperties() {

        return null;
    }

    private Set<Class<?>> loadClass(String packagePath, String fltClass) {
        boolean isRecursive = true;
        Set<Class<?>> clsList = new LinkedHashSet<Class<?>>();
        String pkgName = packagePath;
        String pkgDirName = pkgName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(pkgDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(pkgName, filePath, isRecursive, clsList);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(pkgDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    pkgName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || isRecursive) {
                                    if (name.endsWith(".class")
                                        && !entry.isDirectory()) {
                                        String className = name.substring(pkgName.length() + 1, name.length() - 6);
                                        try {
                                            Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(pkgName
                                                                                                                    + '.'
                                                                                                                    + className);
                                            int modify = cls.getModifiers();
                                            if (Modifier.isAbstract(modify)
                                                || (!Modifier.isPublic(modify)))
                                                if (cls.isInterface()
                                                    || cls.isMemberClass()
                                                    || cls.isEnum()
                                                    || cls.isAnnotation()
                                                    || cls.isAnonymousClass())
                                                    continue;
                                            IocBean iocBean = cls.getAnnotation(IocBean.class);
                                            if (iocBean != null) {
                                                clsList.add(cls);
                                            } else {
                                                Field[] fields = cls.getDeclaredFields();
                                                for (Field field : fields) {
                                                    if (field.getAnnotation(Inject.class) != null) {
                                                        LOG.warn("class({}) don't has @IocBean, but field({}) has @Inject! Miss @IocBean.", cls.getName(), field.getName());
                                                        break;
                                                    }
                                                }
                                            }
                                            clsList.add(cls);
                                        }
                                        catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        LOG.error("", e);
                    }
                }
            }
        }
        catch (IOException e) {
            LOG.error("", e);
        }

        return clsList;
    }

    private void findAndAddClassesInPackageByFile(String packageName,
                                                  String packagePath,
                                                  final boolean recursive,
                                                  Set<Class<?>> clsList) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                       || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName
                                                 + "."
                                                 + file.getName(), file.getAbsolutePath(), recursive, clsList);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(packageName
                                                                                            + '.'
                                                                                            + className);
                    int modify = cls.getModifiers();
                    if (Modifier.isAbstract(modify)
                        || (!Modifier.isPublic(modify)))
                        if (cls.isInterface()
                            || cls.isMemberClass()
                            || cls.isEnum()
                            || cls.isAnnotation()
                            || cls.isAnonymousClass())
                            continue;
                    IocBean iocBean = cls.getAnnotation(IocBean.class);
                    if (iocBean != null) {
                        clsList.add(cls);
                    } else {
                        Field[] fields = cls.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.getAnnotation(Inject.class) != null) {
                                LOG.warn("class({}) don't has @IocBean, but field({}) has @Inject! Miss @IocBean.", cls.getName(), field.getName());
                                break;
                            }
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    LOG.error("", e);
                }
            }
        }
    }
}
