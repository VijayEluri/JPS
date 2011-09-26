package org.jetbrains.jps.incremental;

import org.jetbrains.jps.Module;
import org.jetbrains.jps.ModuleChunk;

import java.util.Collection;
import java.util.Set;

/**
 * @author Eugene Zhuravlev
 *         Date: 9/17/11
 */
public abstract class CompileScope {

  public abstract Collection<Module> getAffectedModules();

  public boolean isAffected(ModuleChunk chunk) {
    final Set<Module> modules = chunk.getModules();
    for (Module module : getAffectedModules()) {
      if (modules.contains(module)) {
        return true;
      }
    }
    return false;
  }
}
